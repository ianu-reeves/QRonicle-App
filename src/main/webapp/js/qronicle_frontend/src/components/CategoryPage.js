import React, { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import Header from './Header'
import ItemCard from './ItemCard'
import {Button, Stack } from "react-bootstrap"
import AddItemModal from './AddItemModal'



export default function TempCatPage() {
    const [showAddItemModal, setShowAddItemModal] = useState(false)
    const {categoryName} = useParams()
    const navigate = useNavigate()
    const [itemData, setItemData] = useState([])

    useEffect(() => {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login')
      }
      //fetch all of users' items with specified tag
      const url = `http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/tags/${categoryName}/items`
      console.log(url)
      const fetchData = async () => {
        try {
          const response = await fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/tags/${categoryName}/items`, {
            headers: {
              Authorization: `Bearer ${token}`,
              "Access-Control-Allow-Origin": "*",
            },
          });
  
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          const tagItems = await response.json();
          setItemData(tagItems);
        } catch (error) {
          console.error(error);
        }
      };
  
      fetchData();
    }, []);

  return (
    <>
    <Header></Header>
    <Stack direction="horizontal" gap="2" className="mb-4">
        <Button className="add-btn" onClick={() => setShowAddItemModal(true)}
        >Add Item</Button>
         <div className="page-title">{categoryName}</div>
    </Stack>
    <div style={{
        display:"grid", 
        gridTemplateColumns: "repeat(auto-fill,  minmax(300px, 1fr))",
        gap:  "1rem", 
        alignItems: "flex-start",
        }}>
        {
        itemData.map(({id, name, description, date, location, value})=>(
            <ItemCard
            key={id}
            id={id}
            name={name}
            description={description}
            date={date}
            location={location}
            value={value}>
            </ItemCard>
        ))
        }
      </div>
      <AddItemModal show={showAddItemModal} 
        handleClose={() => setShowAddItemModal(false)} 
    />
    </>
  )
}
