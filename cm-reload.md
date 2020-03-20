### 목적
ConfigMap의 변경을 인지하여 서비스가 자동으로 재기동 되도록 한다.
   
   
#### 사용되는 OpenSource
> - [stakater/Reloader](https://github.com/stakater/Reloader)

#### 코드/설정 파일
##### src
1. RestController.java
2. PropertiesConfig.java

##### resource
1. application.properties


##### build
1. Dockerfile
2. gradle.build

##### k8s
1. role.yaml
2. deployment.yaml
3. configmap.yaml
4. service.yaml
5. ingress.yaml

