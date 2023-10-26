import React, { useState, useEffect } from 'react'
import { Card, Button, Stack } from 'react-bootstrap'
import Header from './Header'
import QRCode from 'qrcode'
import {useNavigate } from 'react-router-dom'
import AllItemsPage from './AllItemsPage'
import AddItemModal from './AddItemModal';
import jwtDecode from 'jwt-decode'

export default function ProfilePage() {
    const [qrUrl, setQrUrl] = useState('')
    const [showAddItemModal, setShowAddItemModal] = useState(false)
    const [user, setUser] = useState()
    const navigate = useNavigate()
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login')
        }
        const decodedToken = jwtDecode(token)
        fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/users/${decodedToken.sub}`, {
          method: "GET",
          headers: {
              "Authorization": `Bearer ${token}`,
              "Access-Control-Allow-Origin": "*"
          }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            setUser(data)
            setIsLoading(false)
        })
        .catch(error => {
            console.error(error);
            setIsLoading(false)
            
        });
    }, [])

    if (isLoading) {
        return (
            <div>Loading...</div>
        )
    }

  return (
    <>
    <Header></Header>
    <Stack direction="vertical" gap='2' className='profile-page-stack'>
        <Card className='my-1 profile-card'>
            <Card.Body >
                <Stack direction='horizontal' gap='1' className='profile-stack'>
                    <Stack direction="vertical" gap="1" className="mt-1 me-1">
                        <div className='profile-name'>{user.firstName} {user.lastName}</div>
                        <div className='profile-username'>{user.username}</div>
                        <div className='profile-bio'>{user.bio}</div>
                    </Stack>
                </Stack>
                <Stack direction='horizontal' gap='1' className='profile-btns-stack'>
                    {/* <div className="profile-edit-container mt-2 mb-1 ms-4">
                        <Link to="/profile-settings" key="profile-settings" className="profile-edit-link">
                            <div>Edit Profile</div></Link>
                    </div> */}
                    <Button className="profile-add-btn ms-auto me-4 mt-2 mb-1" onClick={() => setShowAddItemModal(true)}
                        >Add Item</Button>
                </Stack>
            </Card.Body>
        </Card>
        <Stack direction="horizontal" gap="2" className="mb-4">
            <div className="page-title">All Items</div>
        </Stack>
        <AllItemsPage/>
    </Stack>
    <AddItemModal show={showAddItemModal} 
    handleClose={() => setShowAddItemModal(false)} 
    />
    </>
  )
}