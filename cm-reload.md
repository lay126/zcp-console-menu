### 목적
ConfigMap의 변경을 인지하여 서비스가 자동으로 재기동 되도록 한다.
   
   
#### 사용되는 OpenSource
> - [stakater/Reloader](https://github.com/stakater/Reloader)


### 적용 순서
1. stakater/Reloader 설치
```
$ helm install stakater/reloader
```
Default namespace에 설치 하는 경우, 모든 namspace를 대상으로 동작한다.
특정 namespace에 대해서만 동작하게 하고 싶은 경우, 특정 namespace를 지정하여 설치한다.


2. 소스 코드 수정

src/PropertiesConfig.java
```
@Configuration
@ConfigurationProperties(prefix = "bean")
public class PropertiesConfig {

    private String data = "This is default data";

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

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
zcp-console-menu-cm.data=Message from backend is:
```

3. k8s 리소스 생성 및 수정 
- role.yaml
- deployment.yaml
- configmap.yaml
- service.yaml
- ingress.yaml

4. 빌드/배포
- Dockerfile
- gradle.build
