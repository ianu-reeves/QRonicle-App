//import React from 'react'
import{ Card}from "react-bootstrap";
import { useState, useEffect } from "react";
import { Link } from "react-router-dom";

export default function ItemCard({ id, name, date}) {  
  const [isLoading, setIsLoading] = useState(true)
  const [imageArray, setImageArray] = useState([])

  useEffect( () => {
    const token = localStorage.getItem('token');
    //get imgs array
    fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/items/${id}/images`, {
      method: "GET",
      headers: {
          "Authorization": `Bearer ${token}`
      }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        return response.json();
    })
    .then(data => {
        setImageArray(data)
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
    )}

  return (
    <>
    <Card>
        <Card.Body>
        <Link to={`/items/${id}`} key={id} 
                   className="view-link">
            <Card.Title className="d-flex justify-content-between 
            align-items-baseline fw-normal mb-3">
                <div className="item-card-name me-2">{name}</div>
                <div className="d-flex align-items-baseline">{date.toString().replaceAll(',','-')}</div>
            </Card.Title>
            <Card.Body>
            {imageArray.length > 0 ? (<img className='card-img-top img-fluid mx-auto d-block ' 
              src={`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/files/images/${imageArray[0].fileName}`} alt="Current Image" 
              style={{width: "350px", height: "auto"}}/>) 
              : null }
              <div className="mt-3 item-card-id">{id}</div>
            </Card.Body>          
            </Link>    
        </Card.Body>
    </Card>
    </>
  )
}