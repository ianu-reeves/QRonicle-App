import React, { useEffect, useState } from 'react'
import ItemCard from './ItemCard'
import AddItemModal from './AddItemModal';
import { useNavigate } from 'react-router-dom';
import jwtDecode from 'jwt-decode';


export default function AllItemPage() {
    const [showAddItemModal, setShowAddItemModal] = useState(false)
    const [items, setItems] = useState([])
    const navigate = useNavigate()

      useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
          navigate('/login')
        }
        //fetch all items for user with jwt token
        const decodedToken = jwtDecode(token);
        const fetchData = async () => {
          try {
            const response = await fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/items/user/${decodedToken.sub}`, {
              headers: {
                Authorization: `Bearer ${token}`,
                "Access-Control-Allow-Origin": "*",
              },
            });
    
            if (!response.ok) {
              throw new Error("Network response was not ok");
            }
            const allItems = await response.json();
            setItems(allItems);
          } catch (error) {
            console.error(error);
          }
        };
        fetchData();
        
      }, []);
     
    return (
    <>
    <div style={{
        display:"grid", 
        gridTemplateColumns: "repeat(auto-fill,  minmax(300px, 1fr))",
        gap:  "1rem", 
        alignItems: "flex-start",
        }}>
        { items && items
          .sort( (a,b) => a.date < b.date ? 1: -1 )
          .map((item) => (
            <ItemCard
            key={item.id}
            id={item.id}
            name={item.name}
            description={item.description}
            date={item.date}
            location={item.location}
            value={item.value}>
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
