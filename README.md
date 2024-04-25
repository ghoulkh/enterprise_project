Khởi chạy app đã có job tự động tạo tài khoản admin:
username: admin
password: admin

### Toàn bộ api có trong file dưới đây ###
[collection postman ở đây!!](postman/api-search-house.postman_collection.json)

Solr core được lưu ở thư mục solr

Tải solr 8.11.2 ở địa chỉ: https://solr.apache.org/downloads.html

Sau đó copy core ở thư mục solr trong sorceCode và start solr


Websocket 

Kết nối tới endpoint sử dụng SockJS http://localhost:8080/ws
sau đó subscriber topic '/post/{postId}'

event được gửi về có dạng sau: 
'{"comment":{"id":8,"postId":5,"createdBy":"phiduong","content":"comment2","createdAt":"2023-04-23T12:27:19.177+00:00","lastUpdateAt":"2023-04-23T12:27:19.177+00:00","parentId":null},"type":"NEW"}'


có 2 type là UPDATE OR NEW

