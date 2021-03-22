# sma-back-springboot

# How to use

1. Clone the project
2. Run ```gradle build``` in terminal
3. Then start the project using ```java -Djasypt.encryptor.password=yourpassword -jar /build/libs/*.war```
4. For the jasypt encryption secret use your own generated passwords. then use it in [application.yml]() like I did

---
---
# APIs

__index page:__ (empty)

```
/
```

---
---

## Roles:

> Role Create: __Admin__ must create roles with authorities before registering new users

```
/api/user/role/
```
Example (``POST``)
```json
{
  "authorities": [
    "OP_ACCESS_USER",
    "OP_EDIT_USER",
    "OP_DELETE_USER"
  ],
  "name": "USER"
}
```

---

> Role Get: Use ``GET`` method
```
/api/user/role/
```

---

> Role Delete: Use ``DELETE`` method
```
/api/user/role/{id}/
```

* Only users with these authorities have access role apis. Default admin has all authorities defined in application
``
OP_ACCESS_ROLE, OP_ADD_ROLE, OP_DELETE_ROLE
``

---
---

## Users: 

> User Signup: Every body can signup. use ``POST`` method. __data transfers in FORM DATA__

```
/api/user/signup/
```
* After successfully signing up, you will get userData in json format in body and two tokens in the header
  * __AccessToken__: Witch used to give user access to request. it can be used once. When used you will receive a new one in the header
  * __RefreshToken__: Which used to check and generate new AccessToken. this token has about 2 weeks lifetime after 2 weeks it expires and you should login

---

> User login: like signup. Use  ``POST`` method.

```
/api/user/login/
```

Example:
```json
{
  "username": "username",
  "password": "password"
}
```
* After login you will get ``RefreshToken`` and ``AccessToken`` and user data as well

---

> User Update: Like signup transfer data using __FORM DATA__. One important thing is that you also have to pass `user id`.

`POST`

```
/api/user/update/
```

---

> User Delete: `DELETE` user by passing its id. only admins and the user can delete

```
/api/user/
```

Example:

```json
{
    "id": 0
}
```

---

> User GetAll: Use `GET` method to receive users' info. Only authenticated users can.

```
/api/user/all/
```

---

> User GetOne: Use `GET` method to receive a user's info. Only authenticated users can.

```
/api/user/
```

Example:

```json
{
    "id": 0
}
```

---
---

## Posts


> Posts All: By ``GET`` requesting to this api you will get all the posts listed in pagination format

```
/api/post/
```
You can also pass some pagination variables in the url to filter posts

---

> Posts Create: Use __FORM DATA__ and `POST` method

```
/api/post/
```
also send ``user_id``

---

> Posts Delete: Only admin with ``OP_DELETE_POST` and the actual user can delete

```
/api/post/{id}/
```

---

> Posts `GET` one: 

```
/api/post/{id}/
```

---

> Posts search:  `GET` method. pass title(required) and content in url

```
/api/post/search/?title=<your title>&content=<your contetn>
```

---
---

## Comments

> Comments Create: Use `POST` method using json body to save a new comment

```
/api/post/comment/
```

Example:

```json
{
  "content": "string",
  "post": {
    "id" : 0
  }
}
```

* Remember to send post id
  
---

> Comments Delete: Only actual user and the admin with `OP_DELETE_COMMENT` authority can delete comment. Use `DELETE` method

```
/api/post/comment/
```

Example:

```json
{
  "id": 0,
  "post": {
    "id": 0
  }
}
```

---
---
---

# Final words 

Hope it's useful. To contact me on Instagram or Telegram use these links

[Telegram](https://t.me/plantdg)

[Instagram](https://instagram/darkdeveloper2/?igshid=38776le23g1w)
