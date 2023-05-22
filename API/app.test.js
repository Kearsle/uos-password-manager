const request = require('supertest')
const app = require('./app.js')

var refreshToken
var accessToken
var userID
var programPasswordID

// Ping

describe("GET /", () => {
    test("Should respond with a 200 status code", async () => {
        const response = await request(app).get("/")
        expect(response.statusCode).toBe(200)
    })
})

// ------- PASSWORDS -------

// User Register

describe("POST /createUser", () => {
    describe("Provided a valid username, password, and email", () => {
        // Should respond with a 201 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 201 status code", async () => {
            const response = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const token = loginResponse.body.accessToken.toString()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${token}`).send()
            expect(response.statusCode).toBe(201)
        })

        test("Should respond in JSON", async () => {
            const response = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const token = loginResponse.body.accessToken.toString()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${token}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a success", async () => {
            const response = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const token = loginResponse.body.accessToken.toString()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${token}`).send()
            expect(response.body.success).toBeDefined()
        })
    })
    describe("Provided invalid username, password, and/or email", () => {
        describe("Invalid password", () => {
            // Should respond with a 400 status code
            // Should respond in JSON
            // Should respond with a error
            test("Should respond with a 400 status code", async () => {
                const response = await request(app).post("/createUser").send({
                    username: "test",
                    password: "test",
                    email: "test@test.com"
                })
                expect(response.statusCode).toBe(400)
            })
    
            test("Should respond in JSON", async () => {
                const response = await request(app).post("/createUser").send({
                    username: "test",
                    password: "test",
                    email: "test@test.com"
                })
                expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
            })
    
            test("Should respond with a error", async () => {
                const response = await request(app).post("/createUser").send({
                    username: "test",
                    password: "test",
                    email: "test@test.com"
                })
                expect(response.body.error).toBeDefined()
            })
        })
        describe("Invalid email", () => {
            // Should respond with a 400 status code
            // Should respond in JSON
            // Should respond with a error
            test("Should respond with a 400 status code", async () => {
                const response = await request(app).post("/createUser").send({
                    username: "test",
                    password: "test123",
                    email: "test.com"
                })
                expect(response.statusCode).toBe(400)
            })
    
            test("Should respond in JSON", async () => {
                const response = await request(app).post("/createUser").send({
                    username: "test",
                    password: "test123",
                    email: "test.com"
                })
                expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
            })
    
            test("Should respond with a error", async () => {
                const response = await request(app).post("/createUser").send({
                    username: "test",
                    password: "test123",
                    email: "test.com"
                })
                expect(response.body.error).toBeDefined()
            })
        })
    })
    describe("Either or all username, password, and/or email is missing", () => {
        // Should respond with a 400 status code
        // Should respond in JSON
        // Should respond with a error
        test("Should respond with a 400 status code", async () => {
            const bodyData = [
                {username: "test", password: "test123"},
                {username: "test", email: "test@test.com"},
                {email: "test@test.com", password: "test123"},
                {email: "test@test.com"},
                {username: "test"},
                {password: "test123"},
                {}
            ]
            for (const body of bodyData) {
                const response = await request(app).post("/createUser").send(body)
                expect(response.statusCode).toBe(400)
            }
        })

        test("Should respond in JSON", async () => {
            const bodyData = [
                {username: "kearsle"},
                {password: "password"},
                {}
            ]
            for (const body of bodyData) {
                const response = await request(app).post("/createUser").send(body)
                expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
            }
        })

        test("Should respond with a error", async () => {
            const bodyData = [
                {username: "kearsle"},
                {password: "password"},
                {}
            ]
            for (const body of bodyData) {
                const response = await request(app).post("/createUser").send(body)
                expect(response.body.error).toBeDefined()
            }
        })
    })
})

// User Login
describe("POST /userLogin", () => {
    // Given a correct username and password
    // Either or both username and password is missing
    // Given username and password is incorrect

    describe("Given a correct username and password", () => {
        // Should respond with a 200 status code
        // Should respond in JSON
        // Should respond with a userID, refresh token and access token
        test("Should respond with a 200 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const response = await request(app).post("/userLogin").send({
                username: "test",
                password: "test123"
            })
            const accessToken = response.body.accessToken.toString()
            const refreshToken = response.body.refreshToken.toString()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(200)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const response = await request(app).post("/userLogin").send({
                username: "test",
                password: "test123"
            })
            const accessToken = response.body.accessToken.toString()
            const refreshToken = response.body.refreshToken.toString()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a userID, refresh token and access token", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const response = await request(app).post("/userLogin").send({
                username: "test",
                password: "test123"
            })
            const accessToken = response.body.accessToken.toString()
            const refreshToken = response.body.refreshToken.toString()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.userID).toBeDefined()
            expect(response.body.accessToken).toBeDefined()
            expect(response.body.refreshToken).toBeDefined()
        })
    })

    describe("Given username and password is incorrect", () => {
        // Should respond with a 401 status code
        // Should respond in JSON
        // Should respond with a error
        test("Should respond with a 401 status code", async () => {
            const response = await request(app).post("/userLogin").send({username: "notarealuser", password: "notarealuserpassword"})
            expect(response.statusCode).toBe(401)
        })

        test("Should respond in JSON", async () => {
            const response = await request(app).post("/userLogin").send({username: "notarealuser", password: "notarealuserpassword"})
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })

        test("Should respond with a error", async () => {
            const response = await request(app).post("/userLogin").send({username: "notarealuser", password: "notarealuserpassword"})
            expect(response.body.error).toBeDefined()
        })
    })

    describe("Either or both username and password is missing", () => {
        // Should respond with a 400 status code
        // Should respond in JSON
        // Should respond with a error
        test("Should respond with a 400 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })            
            const bodyData = [
                {username: "test"},
                {password: "test123"},
                {}
            ]
            for (const body of bodyData) {
                const response = await request(app).post("/userLogin").send(body)
                expect(response.statusCode).toBe(400)
            }
        })

        test("Should respond in JSON", async () => {
            const bodyData = [
                {username: "test"},
                {password: "test123"},
                {}
            ]
            for (const body of bodyData) {
                const response = await request(app).post("/userLogin").send(body)
                expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
            }
        })

        test("Should respond with a error", async () => {
            const bodyData = [
                {username: "test"},
                {password: "test123"},
                {}
            ]
            for (const body of bodyData) {
                const response = await request(app).post("/userLogin").send(body)
                expect(response.body.error).toBeDefined()
            }
        })
    })
})

// Logout User

describe("DEL /userLogout", () => {
    describe("Valid authorisation and valid refresh token supplied", () => {
        // Should respond with a 200 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 200 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send({"refreshToken": refreshToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(200)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send({"refreshToken": refreshToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a success", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send({"refreshToken": refreshToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.success).toBeDefined()
        })
    })

    describe("Valid authorisation and invalid or missing refresh token supplied.", () => {
        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send({"refreshToken": accessToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send({"refreshToken": accessToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send({"refreshToken": accessToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })

        // Should respond with a 401 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 401 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(401)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })
    })

    describe("Invalid authorisation and valid refresh token supplied", () => {
        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send({"refreshToken": refreshToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send({"refreshToken": refreshToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send({"refreshToken": refreshToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })
    })

    describe("Invalid authorisation and invalid or missing refresh token supplied.", () => {
        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send({"refreshToken": accessToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send({"refreshToken": accessToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send({"refreshToken": accessToken})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })

        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogout").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })
    })
})

// Logout All Devices

describe("DEL /userLogoutAll", () => {
    describe("Valid authorisation supplied.", () => {
        // Should respond with a 200 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 200 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogoutAll").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(200)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogoutAll").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a success", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogoutAll").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.success).toBeDefined()
        })
    })

    describe("Invalid authorisation supplied.", () => {
        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogoutAll").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogoutAll").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userLogoutAll").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })
    })
})

// Get User Information

describe("GET /user", () => {
    describe("Valid authorisation supplied.", () => {
        // Should respond with a 200 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 200 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).post("/user").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(200)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).post("/user").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a userID, username, and email", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).post("/user").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body._id).toBeDefined()
            expect(response.body.username).toBeDefined()
            expect(response.body.email).toBeDefined()
        })
    })

    describe("Invalid authorisation supplied.", () => {
        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).post("/user").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).post("/user").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).post("/user").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })
    })
})

// Reset User Password

describe("PUT /userPasswordReset", () => {
    describe("Valid authorisation and new password and old password supplied.", () => {
        // Should respond with a 201 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 201 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).put("/userPasswordReset").set('Authorization', `Bearer ${accessToken}`).send({password: "test321", oldPassword: "test123"})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(201)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).put("/userPasswordReset").set('Authorization', `Bearer ${accessToken}`).send({password: "test321", oldPassword: "test123"})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a success", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).put("/userPasswordReset").set('Authorization', `Bearer ${accessToken}`).send({password: "test321", oldPassword: "test123"})
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.success).toBeDefined()
        })
    })
})

// Delete User

describe("DEL /userDelete", () => {
    describe("Valid authorisation supplied.", () => {
        // Should respond with a 200 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 200 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(200)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a success", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.success).toBeDefined()
        })
    })

    describe("Invalid authorisation supplied.", () => {
        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userDelete").set('Authorization', `Bearer abc`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userDelete").set('Authorization', `Bearer abc`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            const response = await request(app).delete("/userDelete").set('Authorization', `Bearer abc`).send()
            expect(response.body.error).toBeDefined()
        })
    })
})


// ------- PASSWORDS -------

// Get Program Passwords

describe("GET /passwords", () => {
    describe("Valid authorisation supplied.", () => {
        // Should respond with a 200 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 200 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            await request(app).post("/passwords/create").set('Authorization', `Bearer ${accessToken}`).send({
                programName: "testName",
                programUsername: "testUsername",
                programPassword: "testPassword"
            })
            const response = await request(app).get("/passwords").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(200)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            await request(app).post("/passwords/create").set('Authorization', `Bearer ${accessToken}`).send({
                programName: "testName",
                programUsername: "testUsername",
                programPassword: "testPassword"
            })
            const response = await request(app).get("/passwords").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a passwords", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            await request(app).post("/passwords/create").set('Authorization', `Bearer ${accessToken}`).send({
                programName: "testName",
                programUsername: "testUsername",
                programPassword: "testPassword"
            })
            const response = await request(app).get("/passwords").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body).toBeDefined()
        })
    })

    describe("Invalid authorisation supplied.", () => {
        // Should respond with a 403 status code
        // Should respond in JSON
        // Should respond with a success
        test("Should respond with a 403 status code", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            await request(app).post("/passwords/create").set('Authorization', `Bearer ${accessToken}`).send({
                programName: "testName",
                programUsername: "testUsername",
                programPassword: "testPassword"
            })
            const response = await request(app).get("/passwords").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.statusCode).toBe(403)
        })

        test("Should respond in JSON", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            await request(app).post("/passwords/create").set('Authorization', `Bearer ${accessToken}`).send({
                programName: "testName",
                programUsername: "testUsername",
                programPassword: "testPassword"
            })
            const response = await request(app).get("/passwords").set('Authorization', `Bearer ${accessToken}`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer abc`).send()
            expect(response.headers['content-type']).toEqual(expect.stringContaining("json"))
        })
        
        test("Should respond with a error", async () => {
            const createResponse = await request(app).post("/createUser").send({
                username: "test",
                password: "test123",
                email: "test@test.com"
            })
            const loginResponse =  await request(app).post("/userLogin").send({username: "test", password: "test123"})
            const accessToken = loginResponse.body.accessToken.toString()
            const refreshToken = loginResponse.body.refreshToken.toString()
            await request(app).post("/passwords/create").set('Authorization', `Bearer ${accessToken}`).send({
                programName: "testName",
                programUsername: "testUsername",
                programPassword: "testPassword"
            })
            const response = await request(app).get("/passwords").set('Authorization', `Bearer abc`).send()
            await request(app).delete("/userDelete").set('Authorization', `Bearer ${accessToken}`).send()
            expect(response.body.error).toBeDefined()
        })
    })
})