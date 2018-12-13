curl -i  -X GET "http://localhost:8080/connect/AA44SS"



curl -i -H "Accept:application/json" \
-H "Content-Type:application/json" \
-X POST \
--data '{ "deviceHash":"AA44SS", "playerName": "Kornél", "colorName": "Kék"}' \
"http://localhost:8080/addPlayer"


curl -i -H "Accept:application/json" \
-H "Content-Type:application/json" \
-X POST \
--data '{ "deviceHash":"AA44SS", "playerName": "Dénes", "colorName": "Zöld"}' \
"http://localhost:8080/addPlayer"

curl -i -H "Accept:application/json" \
-H "Content-Type:application/json" \
-X POST \
--data '{ "deviceHash":"AA44SS", "playerName": "Judit", "colorName": "Narancs"}' \
"http://localhost:8080/addPlayer"

