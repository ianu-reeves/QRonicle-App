import React, { useState } from 'react'
import {Card, Form, Button, Alert } from "react-bootstrap"
import { Link, useNavigate } from 'react-router-dom'

export default function Login() {
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const navigate = useNavigate()
    
    const handleSubmit = (e) => {
        setError('')
        e.preventDefault()
        const user = { username, password }
        try {
            fetch("http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/authenticate", {
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
                    console.log('data')
                    console.log(data)
                    const token = data.token;
                    console.log('token')
                    console.log(token)
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

  return (
    <>
    <Card className="user-card">
        <Card.Body>
            <h2 className="text-center mb-4">Login</h2>
            { error && <Alert variant="danger" >{error}</Alert>}
            <Form onSubmit={handleSubmit}> 
                <Form.Group id="username">
                    <Form.Label>Username</Form.Label>
                    <Form.Control type="text" value={username} onChange={(e)=>setUsername(e.target.value)} required />
                </Form.Group>
                <Form.Group id="password">
                    <Form.Label>Password</Form.Label>
                    <Form.Control type="password" value={password} onChange={(e)=>setPassword(e.target.value)} required />
                </Form.Group>
                <Button disabled={loading} className="w-100 mt-3" type="submit">Log In</Button>
            </Form>
        </Card.Body>
    </Card>
    <div className="w-100 text-center mt-2">
        Need an account? <Link to="/signup">Sign Up</Link>
    </div>    
    </>
  )
}