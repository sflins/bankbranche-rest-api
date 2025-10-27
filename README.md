
## System Overview

Criação de uma API REST no Spring Boot que cadastrará as agências bancárias e suas localizações.

Ao realizar uma consulta, o usuário deverá retornar as distâncias até as agências bancárias cadastradas.

### Main Technologies
- **Java 22** - Programming Language
- **Spring Boot 3.5.6** - Core Framework
- **Spring Data JPA** - Data Persistence
- **H2 Database** - In-Memory Database
- **Maven** - Dependency Management
- **Swagger/OpenAPI 3** - API Documentation

### External Dependencies

- **H2 Database**: Starts automatically with the application
- **Mocks**: Used in unit tests (Mockito)

## Endpoints and Contracts

### Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

Endpoints:

http://localhost:8080/desafio/cadastrar
http://localhost:8080/desafio/distancia

Documentação para criação de OAuth Apps : https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app

Usar Client Id e Client Secrets para gerar token

#!/bin/bash
set -euo pipefail

VERIFIER=$(openssl rand -base64 32 | tr '+/' '-_' | tr -d '=')
CHALLENGE=$(printf %s "$VERIFIER" | openssl dgst -sha256 -binary | base64 | tr '+/' '-_' | tr -d '=')

echo "SAVE THIS CODE VERIFIER:"
echo "$VERIFIER"
echo

URL="https://github.com/login/oauth/authorize\
?client_id=$client-id\
&redirect_uri=https%3A%2F%2Foauth.pstmn.io%2Fv1%2Fcallback\
&scope=user%3Aemail%20read%3Auser\
&state=state_$(date +%s)\
&code_challenge=$CHALLENGE\
&code_challenge_method=S256"

echo "OPEN IN CHROME:"
echo "$URL"
echo
echo "WARNING: DO NOT PASTE THE VERIFIER!"
echo "In Postman, copy ONLY the 'code:' value (short string)"
echo

read -p "Paste the CODE (from Postman) here: " AUTH_CODE

echo
echo "Getting token..."
curl -s -X POST "https://github.com/login/oauth/access_token" \
-H "Accept: application/json" \
-d "client_id=$client-id" \
-d "client_secret=$client-secret" \
-d "code=$AUTH_CODE" \
-d "redirect_uri=https://oauth.pstmn.io/v1/callback" \
-d "code_verifier=$VERIFIER"



OPEN IN CHROME:
https://github.com/login/oauth/authorize?client_id=Ov23liz106XkHOH07yfm&redirect_uri=https%3A%2F%2Foauth.pstmn.io%2Fv1%2Fcallback&scope=user%3Aemail%20read%3Auser&state=state_1761526407&code_challenge=9wK9RVNJfQHoPqOUgOr3NI_RFdFf4Ihml1nsgc7opuY&code_challenge_method=S256

Copiar o código de verificação delimitado pelas string code e =state e coçar no gitBash(read -p "Paste the CODE (from Postman) here: " AUTH_CODE)

Token gerado:

Paste the CODE (from Postman) here: a9a9dc447c21e3c74f96

Getting token...
{"access_token":"***REMOVIDO***","token_type":"bearer","scope":"read:user,user:email"}

Usar token nas requsições:


$ curl -X POST \
-H "Authorization: Bearer ***REMOVIDO***" \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{"posX": 20.0, "posY": 20.0}' \
http://localhost:8080/desafio/cadastrar
% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
Dload  Upload   Total   Spent    Left  Speed
100   168    0   140  100    28    469     93 --:--:-- --:--:-- --:--:--   563{"id":4,"nome":"AGENCIA_4","posX":20.0,"posY":20.0,"dataCriacao":"2025-10-26T22:01:42.5883648","mensagem":"Branch registered successfully!"}



$ curl -X GET \
-H "Authorization: Bearer ***REMOVIDO***" \
-H "Accept: application/json" \
http://localhost:8080/desafio/distancia?posX=40\&posY=30
% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
Dload  Upload   Total   Spent    Left  Speed
100   142    0   142    0     0    444      0 --:--:-- --:--:-- --:--:--   445{"agencias":{"AGENCIA_3":"distancia = 10.00","AGENCIA_2":"distancia = 20.00","AGENCIA_4":"distancia = 22.36","AGENCIA_1":"distancia = 41.23"}}

Os testes podem ser feitos também pelo Postman

Geração de Toekn no Postman

https://learning.postman.com/docs/sending-requests/authorization/oauth-20/

