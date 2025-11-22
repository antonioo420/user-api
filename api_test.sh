#!/bin/bash

BASE_URL="http://localhost:8080"

echo "----------------------- REGISTRO DE USUARIOS NORMALES -----------------------"
curl -s -X POST "$BASE_URL/auth/register" \
    -H "Content-Type: application/json" \
    -d '{"username":"antonito","email":"antonito@example.com","password":"12345678"}' | jq
echo -e "\n"

curl -s -X POST "$BASE_URL/auth/register" \
    -H "Content-Type: application/json" \
    -d '{"username":"maria","email":"maria@example.com","password":"abcdefgh"}' | jq
echo -e "\n"

echo "----------------------- LOGIN DEL USUARIO NORMAL PARA OBTENER TOKEN -----------------------"
USER_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"antonito","password":"12345678"}' | jq -r '.token')

echo "TOKEN DEL USUARIO:"
echo "$USER_TOKEN"
echo -e "\n"

echo "----------------------- ENDPOINT SOLO PARA USUARIOS AUTENTICADOS: /test/user -----------------------"
curl -s -X GET "$BASE_URL/test/user" \
    -H "Authorization: Bearer $USER_TOKEN"
echo -e "\n"

echo "----------------------- ENDPOINT ABIERTO: /test/all -----------------------"
curl -s -X GET "$BASE_URL/test/all" 
echo -e "\n"

echo "----------------------- LOGIN DEL USUARIO ADMIN PARA OBTENER TOKEN -----------------------"

ADMIN_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"admin123"}' | jq -r '.token')
echo "TOKEN DEL ADMIN:"
echo "$ADMIN_TOKEN"
echo -e "\n"

echo "----------------------- LISTAR TODOS LOS USUARIOS -----------------------"
curl -s -X GET "$BASE_URL/users" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq
echo -e "\n"

echo "----------------------- OBTENER USUARIO POR ID -----------------------"
USER_ID=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"antonito","password":"12345678"}' | jq -r '.id')

curl -s -X GET "$BASE_URL/users/$USER_ID" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq

echo "----------------------- ELIMINAR USUARIO POR ID -----------------------"
curl -s -X DELETE "$BASE_URL/users/$USER_ID" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq
echo -e "\n"

curl -s -X GET "$BASE_URL/users" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq

echo "----------------------- EXCEPCIONES -----------------------"
echo -e "\n"

echo "----------------------- USUARIO INCORRECTO -----------------------"

curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"maria1","password":"abcdefgh123"}' | jq
echo -e "\n"

echo "----------------------- CONTRASEÑA INCORRECTA -----------------------"

curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"maria","password":"abcdefgh123"}' | jq
echo -e "\n"

echo "----------------------- PERMISOS INSUFICIENTES -----------------------"

echo "----------------------- /users -----------------------"

curl -I -s -X GET "$BASE_URL/users/" \
    -H "Authorization: Bearer $USER_TOKEN" 

echo "----------------------- /test/user -----------------------"

curl -I -s -X GET "$BASE_URL/test/user" 