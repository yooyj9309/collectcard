# collectcard
[![codecov Badge](https://codecov.io/gh/Rainist/collectcard/branch/master/graph/badge.svg?token=BR9PE4VDH9)](https://codecov.io/gh/Rainist/collectcard)
![Java Badge](https://img.shields.io/badge/language-kotlin-brightgreen)
![gRPC Badge](https://img.shields.io/badge/Use-gRPC-2DA6B0)
![StringBoot Badge](https://img.shields.io/badge/SpringBoot-2.2.5.RELEASE-blue)

# Understanding the Collectcard application
Local에서 Collectcard를 실행하기 위해선 K8s development 네임스페이스에 있는 Cipher와 Connect와 포트포워딩을 해야 합니다. Cipher는 DB 암호화키를 관리하는 서비스이며 Connect는 금융사 토큰 발급, 갱신, 조회 기능을 지원하는 서비스입니다. Collectcard가 정상적으로 동작하기 위해선 이 두 서비스가 필요합니다. 전체적인 과정은 아래와 같습니다. 

- Connect 서비스 포트포워딩
- Cipher 서비스 포트포워딩
- Collectcard 실행하기 위한 내부 Env 설정
- Collectcard 실행


## Connect 서비스 포트포워딩
- [Connect 서비스 실행 방법](https://rainist.atlassian.net/wiki/spaces/API/pages/640647181/connect+grpc)

## Cipher 서비스 포트포워딩
- [Cipher 서비스 실행 방법](https://rainist.atlassian.net/wiki/spaces/API/pages/956956946/Cipher+grpc)

## Collectcard 실행하기 위한 내부 Env 설정
- 위 두 과정을 통해 Local의 collectcard와 Connect와 Cipher는 포트포워딩으로 연결될 준비가 되어 있습니다.  두 서비스와 collectcard와 연결하기 위해서 Collectcard의 내부 Environment를 설정해야 합니다. 
- [Collectcard 내부 Environment 설정 방법](https://rainist.atlassian.net/wiki/spaces/API/pages/956760153/Collectcard+Environment)

## Collectcard 실행
- 위 과정을 진행했다면 CollectCardApplication을 실행합니다. 

```java
@SpringBootApplication
class CollectcardApplication

fun main(args: Array<String>) {
    runApplication<CollectcardApplication>(*args)
}
```

## 발생할 수 있는 에러 

### UNIMPLEMENTED: Method not found: cipher.Cipher/GetEncryptedDbTableCipherKey 
- Cipher 서비스가 구동이 안된 상태에서 Collectcard를 실행했기 때문이다. Cipher를 실행 후 다시 시도한다. 
**Proto (API)**
--
- [CollectCard Proto](https://github.com/banksalad/idl/blob/master/protos/apis/v1/collectcard/collectcard.proto)


**Support Organization**
--
- 신한카드


**Code Style**  
-
코드 스타일의 경우, CollectBank 프로젝트에 있는 xml을 같이 사용합니다. 아래 적용가이드 링크 접속후 로컬에 반드시 세팅이 필요합니다.

CodeStyle 적용 가이드 : [링크(CollectBank README.md)](https://github.com/banksalad/collectbank#code-style, "google link")
xml 위치 : [intellij-code-style_collect.xml](https://github.com/banksalad/collectbank/blob/master/intellij-code-style_collect.xml, "google link")

