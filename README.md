# UoS Password Manager Android Application
The project will be able to produce highly secure but hard to remember passwords using a password generator following the OWASP MASVS Level 1 requirements. A secure password managing application for android was successfully developed by use of a REST API. The password manager contains secure communication channels using TLS/SSL encryption protocol with a x509 certificate. Token based authentication was implemented to provide a trustworthy source of authentication. The application has brute force mitigation methods implemented.
## Password Manager API
The Password Manager API is used to communicate to the databases and to perform

A Redis and MongoDB server is required for this to run. 

Title | URL | Description
--- | --- | ---
**Ping** | `https://127.0.0.1:8000` | Test to check if API is running.
**Create User** | `https://127.0.0.1:8000/createUser` | Register's user to database. Requires a username, password and email.
**User Login** | `https://127.0.0.1:8000/userLogin` | User login, provides access and refresh tokens. Requires a username and password.
**User Logout** | `https://127.0.0.1:8000/userLogout` | User logs out by unauthorising tokens. Requires authentication token and refresh token.
**User Logout** | `https://127.0.0.1:8000/userLogoutAll` | User logs out by unauthorising tokens. Requires authentication token.
**User Delete** | `https://127.0.0.1:8000/userDelete` | Deletes user and all stored passwords. Requires authentication token.
**User Password Reset** | `https://127.0.0.1:8000/userPasswordReset` | Resets password for user. Requires authentication token, password and old password.
**User Get** | `https://127.0.0.1:8000/user` | Returns userID, username and email of logged in user. Requires authentication token.
**Password Create** | `https://127.0.0.1:8000/passwords/create` | Stores a program password. Requires authentication token, program name, username and password.
**Password Edit** | `https://127.0.0.1:8000/passwords/edit` | Edits a stored program password. Requires authentication token, program ID, name, username and password.
**Password Delete** | `https://127.0.0.1:8000/passwords/delete` | Deletes a stored program password. Requires authentication token and program ID.
**Password Delete All** | `https://127.0.0.1:8000/passwords/deleteAll` | Deletes all stored program passwords for a user. Requires authentication token.
**Password Get** | `https://127.0.0.1:8000/passwords` | Returns all stored passwords for a user. Requires authentication token.
**New Access Token** | `http://127.0.0.1:8000/token` | Generates a new access token using a refresh token. Requires refresh token and user ID.
**Three Random Words** | `http://127.0.0.1:8000/generateThreeRandomWords` | Generates a Three Random Word Password. Requires authentication token.

### Redis Server Download ###

#### Mac ####

Within the terminal run:
```bash
brew install redis
```

Start the server in the server within the terminal by:
```bash
redis-server
```
#### Windows ####

Download the latest Redis .msi file from https://github.com/microsoftarchive/redis/releases and install it. 

The server will automatically be started. This can be tested within the terminal by running: 
```bash
redis-cli
ping
```

### MongoDB Server Download ###
MongoDB can be installed from: https://www.mongodb.com/docs/manual/installation/.

To start the server, within the terminal run:
```bash
mongod
```

### API ###

To install the required dependancies, run:

```bash
  npm install
```

To start the API, within the API folder run:

```bash
  npm start
```

To perform the unit test script, run:

```bash
  npm test
```
> Note that the brute force mitigation methods need to be disabled to run properly. 

## UoS Password Managing Android Application ##

The application is designed to be run from Android Studio.

This can be installed from: https://developer.android.com/studio.
