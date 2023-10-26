import React, { useRef, useState } from 'react'
import {Card, Form, Button, Alert } from "react-bootstrap"
import { Link, useNavigate } from 'react-router-dom'

export default function ProfileSettings() {
    const [username, setUsername] = useState()
    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [matchingPassword, setMatchingPassword] = useState('')
    const [privacy, setPrivacy] = useState('')
    const [userType, setUserType] = useState('')
    const [bio, setBio] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const [match, setMatch] = useState(true)
    const navigate = useNavigate()
    
    const userData =
        { 
        userId: '1',
        username: "traveller1",
        firstName: 'Susy',
        lastName: "Smtih",
        email: 'susysmith1@gmail.com',
        password: "password123",
        matchingPassword: "password123",
        privacy: "private",
        type: "traveller",
        bio: "hi my name is Susy and i love to travel"
     }
    function handleSubmit(e) {
        e.preventDefault()
        setMatch(true)
        setError("")
        // if (password !== matchingPassword) {
        //     setMatch((current) => !current)
        //     return setError('Passwords do not match.')
        // } 
        //CHECK IF THE STATES VARIABLES MATCH THE USER'S DATA -> IF NOT, USE STATE VARIABLE

        if (match) {
            setError('MATCH')
            try {
                const newUserData={password}
                setLoading(true)
                //await signup(emailRef.current.value, passwordRef.current.value)
                navigate.push("/")
            } catch {
                setError('Failed to update account')
            }
        }
        
        setLoading(false)
    }

  return (
    <>
        
        <Card>
            <Card.Body>
                <h2 className="text-center mb-4">Profile Settings</h2>
                { error && <Alert variant="danger" >{error}</Alert>}
                <Form onSubmit={handleSubmit}> 
                    <Form.Group id="username">
                        <Form.Label>Username</Form.Label>
                        <Form.Control type="text" value={username} onChange={(e)=>setUsername(e.target.value)}
                        placeholder={userData.username}/>
                    </Form.Group>
                    <Form.Group id="first-name">
                        <Form.Label>First Name</Form.Label>
                        <Form.Control type="text" value={firstName} onChange={(e)=>setFirstName(e.target.value)}
                        placeholder={userData.firstName}/>
                    </Form.Group>
                    <Form.Group id="last-name">
                        <Form.Label>Last Name</Form.Label>
                        <Form.Control type="text" value={lastName} onChange={(e)=>setLastName(e.target.value)}
                        placeholder={userData.lastName}/>
                    </Form.Group>
                    <Form.Group id="email">
                        <Form.Label>Email</Form.Label>
                        <Form.Control type="email" value={email} onChange={(e)=>setEmail(e.target.value)}
                            placeholder={userData.email}/>
                    </Form.Group>
                    <Form.Group id="password">
                        <Form.Label>Password</Form.Label>
                        <Form.Control type="password" value={password} onChange={(e)=>setPassword(e.target.value)}
                            placeholder='Leave blank to keep the same'/>
                    </Form.Group>
                    <Form.Group id="password-confirm">
                        <Form.Label>Password Confirmation</Form.Label>
                        <Form.Control type="password" value={matchingPassword} onChange={(e)=>setMatchingPassword(e.target.value)}
                            placeholder='Leave blank to keep the same'/>
                    </Form.Group>
                    <Form.Group controlId="bio">
                        <Form.Label>Bio</Form.Label>
                        <Form.Control as="textarea"
                            value={bio} onChange={(e)=>setBio(e.target.value)} />
                    </Form.Group>  

                    <Form.Group id="account-privacy">
                        <Form.Label>Account Privacy</Form.Label>
                        <Form.Control type="text" value={privacy} onChange={(e)=>setPrivacy(e.target.value)}
                        placeholder={userData.privacy}/>
                    </Form.Group>
                    <Form.Group id="user-type">
                        <Form.Label>User Type</Form.Label>
                        <Form.Control type="text" value={userType} onChange={(e)=>setUserType(e.target.value)}
                        placeholder={userData.type}/>
                    </Form.Group>
                    <Button disabled={loading} className="w-100 mt-2" type="submit">Update</Button>
                </Form>
                <div>{password} {matchingPassword} match:{match}</div>
            </Card.Body> 
        </Card>
        <div className="w-100 text-center mt-2">
            <Link to="/">Cancel</Link>
             </div>    

    </>
  )
}
