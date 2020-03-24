### 목적
ConfigMap의 변경을 인지하여 서비스가 자동으로 재기동 되도록 한다.   
   
#### 사용되는 OpenSource
> - [stakater/Reloader](https://github.com/stakater/Reloader)   
   
### 적용 순서
#### 1. stakater/Reloader 설치
```
$ helm install stakater/reloader
```
Default namespace에 설치 하는 경우, 모든 namspace를 대상으로 동작한다.   
특정 namespace에 대해서만 동작하게 하고 싶은 경우, 특정 namespace를 지정하여 설치한다.   


#### 2. 소스 코드 수정

src/PropertiesConfig.java
```
@Configuration
@ConfigurationProperties(prefix = "bean")
public class PropertiesConfig {

    private String data = "This is default data";
    
    ... setter & getter
}
```

src/RestController.java
```
@org.springframework.web.bind.annotation.RestController
@RequestMapping("/test")
public class RestController {
    @Autowired
    private PropertiesConfig yamlConfig;

    @GetMapping("/data")
    public String load() {
        String data = "error";
        try {
            data = String.format(yamlConfig.getData(), "", "");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return data;
    }

}
```

resources/application.properties
```
bean.data=Message in application.properties
```

#### 3. k8s 리소스 생성 및 수정 
- role.yaml
namespace의 default Role에 configmaps를 get, list, watch 할 수 있는 권한을 부여한다
``` yaml
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  namespace: ayoung
  name: default
rules:
  - apiGroups: ["", "extensions", "apps"]
    resources: ["configmaps", "pods", "services", "endpoints", "secrets"]
    verbs: ["get", "list", "watch"]
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: default-binding
  namespace: ayoung
subjects:
  - kind: ServiceAccount
    name: default
    apiGroup: ""
roleRef:
  kind: Role
  name: default
  apiGroup: ""
```

- configmap.yaml
resources/application.properties
``` yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: console-boot-template-cm         # CHANG IT
  namespace: ayoung
data:
  application.properties: |-
    bean.data=Testing reload! Message from configmap
```

- deployment.yaml
metadata:annoation:configmap.reloader.stakater.com/reload에 변화감지 대상 ConfigMap의 이름을 지정한다
``` yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  annotations:
    configmap.reloader.stakater.com/reload: "console-boot-template-cm"     # CHANGE IT
  name: console-boot-template
  namespace: ayoung
spec:
  selector:
    matchLabels:
      app: console-boot-template

  replicas: 1
  template:
    metadata:
      labels:
        app: console-boot-template
    spec:
      containers:
        - name: console-boot-template
          image: lay126/console-boot-template:latest
          ports:
            - containerPort: 8080
          env:
            - name: env.namespace
              value: ayoung
          volumeMounts:
            - name: config
              mountPath: /config
      volumes:
        - name: config
          configMap:
            name: console-boot-template-cm
```

- service.yaml
``` yaml
kind: Service
apiVersion: v1
metadata:
  name: console-boot-template-svc
  namespace: ayoung
spec:
  selector:
    app: console-boot-template
  ports:
    - protocol: TCP
      port: 8080
      nodePort: 30083
  type: NodePort
  ```
  
- ingress.yaml
``` yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: console-boot-template-ing
  namespace: ayoung
spec:
  rules:
  - host: console-boot-template.cloudzcp.io
    http:
      paths:
      - backend:
          serviceName: console-boot-template-svc
          servicePort: 8080
  tls:
  - hosts:
    - console-boot-template.cloudzcp.io
    secretName: cloudzcp-io-cert
```
