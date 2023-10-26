import React, { useState } from 'react'
import {Card, Form, Button, Alert } from "react-bootstrap"
import { Link, useNavigate } from 'react-router-dom'

export default function Signup() {
    const [error, setError] = useState('')
    const [username, setUsername] = useState('')
    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')
    const [email, setEmail] = useState('')
    const [matchingEmail, setMatchingEmail] = useState('')
    const [password, setPassword] = useState('')
    const [matchingPassword, setMatchingPassword] = useState('')
    const [bio, setBio] = useState('')
    const [privacy, setPrivacy] = useState('private')
    const [loading, setLoading] = useState(false)
    const [match, setMatch] = useState(true)
    const navigate=useNavigate()

    const checkMatch = () => {
        setMatch("true")
        if (password !== matchingPassword) {
            setMatch((current) => !current)
            window.scrollTo(0,0)
            return setError('Passwords do not match.')
        } 
        if (email !== matchingEmail) {
            setMatch((current) => !current)
            window.scrollTo(0,0)
            return setError('Emails do not match.')
        }
    }

    const checkLengths = () => {
        if (password.length < 10 || password.length > 50) {
            window.scrollTo(0,0)
            return setError('Password must be between 10 and 50 characters.')
        }
        if (username.length < 3 || username.length > 24) {
            window.scrollTo(0,0)
            return setError('Username must be between 3 and 24 characters.')
        }
    }

    async function handleSubmit(e) {
        setError("")
        e.preventDefault()
        checkMatch()
        checkLengths()

        if (match) {
            const user={username, firstName, lastName, email, matchingEmail,
                password, matchingPassword, bio, privacy}
            console.log(user)
            //add user to database
            try {
                fetch("http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/register", {
                    method:"POST",
                    headers:{"Content-Type":"application/json"},
                    body:JSON.stringify(user)
                })
                .then(response => {
                    console.log('response')
                    console.log(response)
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    const token = data.token;
                    localStorage.setItem("token", token);
                    navigate('/');
                })
                .catch(error => {
                    console.error('There was a problem with the fetch operation:', error);
                });
            } catch{
                console.log(error)
            }
        }
    }

  return (
    <>
        <Card className="user-card">
            <Card.Body>
                <h2 className="text-center mb-4">Sign Up</h2>
                { error && <Alert variant="danger" >{error}</Alert>}
                <Form onSubmit={handleSubmit}>
                    <Form.Group id="username">
                        <Form.Label>Username</Form.Label>
                        <Form.Control type="text" value={username} onChange={(e)=>setUsername(e.target.value)} required />
                    </Form.Group>
                    <Form.Group id="firstName">
                        <Form.Label>First Name</Form.Label>
                        <Form.Control type="text" value={firstName} onChange={(e)=>setFirstName(e.target.value)} required />
                    </Form.Group>
                    <Form.Group id="lastName">
                        <Form.Label>Last Name</Form.Label>
                        <Form.Control type="text" value={lastName} onChange={(e)=>setLastName(e.target.value)} required />
                    </Form.Group>
                    <Form.Group id="email">
                        <Form.Label>Email</Form.Label>
                        <Form.Control type="email" value={email} onChange={(e)=>setEmail(e.target.value)} required />
                    </Form.Group>
                    <Form.Group id="matchingEmail">
                        <Form.Label>Email Confirmation</Form.Label>
                        <Form.Control type="email" value={matchingEmail} onChange={(e)=>setMatchingEmail(e.target.value)} required />
                    </Form.Group>
                    <Form.Group id="password">
                        <Form.Label>Password</Form.Label>
                        <Form.Control type="password" value={password} onChange={(e)=>setPassword(e.target.value)} required />
                    </Form.Group>
                    <Form.Group id="matchingPassword">
                        <Form.Label>Password Confirmation</Form.Label>
                        <Form.Control type="password" value={matchingPassword} onChange={(e)=>setMatchingPassword(e.target.value)} required />
                    </Form.Group>
                    <Form.Group controlId="bio">
                        <Form.Label>Bio</Form.Label>
                        <Form.Control as="textarea"
                            value={bio} onChange={(e)=>setBio(e.target.value)} />
                    </Form.Group>
                    <Button disabled={loading} className="w-100 mt-3" type="submit">Sign Up</Button>
                </Form>
            </Card.Body>
        </Card>
        <div className="w-100 text-center mt-2">
            Already have an account? <Link to="/login">Log in</Link>
        </div>
    </>
  )
}