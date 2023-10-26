import React from 'react'
import Header from "./Header"
import '../App.css';
import {Button, Stack } from "react-bootstrap"
import Container from "react-bootstrap/Container"
import AddItemModal from './AddItemModal';
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import jwtDecode from 'jwt-decode';

//import {getPosts} from "./api"
export default function Categories() {
  const [showAddItemModal, setShowAddItemModal] = useState(false)  
  const [tags, setTags] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login')
    }
    //fetch all tags used by user
    const decodedToken = jwtDecode(token)
    const sub = decodedToken.sub
    const fetchData = async () => {
      try {
        const response = await fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/users/${sub}/tags`, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Access-Control-Allow-Origin": "*",
          },
        });
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const allItems = await response.json();
        setTags(allItems);
        setIsLoading(false)
      } catch (error) {
        console.error(error);
        setIsLoading(false)
      }
    };

    fetchData();
    
  }, []);

  if (isLoading) {
    return (
      <>
      <Header/>
      <div>Loading...</div>
      </>
    )
}

  return (
    <>
    <Header/>
    <Container className="my-4">
      <Stack direction="horizontal" gap="2" className="mb-4">
        <Button className="add-btn px-2" onClick={() => setShowAddItemModal(true)}
        >Add Item</Button>
         <div className="page-title">Categories</div>
      </Stack>
      <Stack direction='vertical' gap='2' className=''>
        {
          tags.map((tag, index)=>(
            <div className="category-container py-1 px-3 mx-auto d-block">
            <Link to={`/category/${tag.description}`} key={index} 
              className="profile-edit-link">
            <div>{tag.description}</div>
          </Link>
          </div>
          ))
        }
      </Stack>
    </Container>
    <AddItemModal show={showAddItemModal} 
    handleClose={() => setShowAddItemModal(false)} 
    />
    </>
  )
}
