import React from 'react'
import '../App.css';
import { Button, Card } from "react-bootstrap"
import AddItemModal from './AddItemModal';
import Header from "./Header"
import { useEffect, useState } from 'react';
import {BrowserRouter as Router, useNavigate, Link } from "react-router-dom"
import  { QrReader } from 'react-qr-reader'



export default function Dashboard() {
  const [showAddItemModal, setShowAddItemModal] = useState(false)
  const [qrScanResult, setQrScanResult] = useState('')
  const navigate = useNavigate()
 
  const handleQrScanError = (error) => {
    console.log(error)
  }

  const handleQrScan = (result) => {
    if (result) {
      setQrScanResult(result)
      console.log(result)
    }
  }

    useEffect( () => {
      if (!localStorage.getItem("token")) {
          navigate('/login')
          //setUser(jwt_decode(token))
        } 
  }, [])


  return (
    <>
    <Header/>
    <Button className="add-btn mt-2" onClick={() => setShowAddItemModal(true)}
    >Add Item</Button>
    <Card className='mt-2'>
      <Card.Title className='card-title'>Scan an Items QR code!</Card.Title>
      <Card.Body>
      <QrReader 
        delay={300}
        style={{width: '100%'}}
        onError={handleQrScanError}
        onResult={handleQrScan}
      />
      <Link to={`${qrScanResult.text}`} className="qr-link">Scanned code: {qrScanResult.text}</Link>
      </Card.Body>
    </Card>
    <AddItemModal show={showAddItemModal} 
    handleClose={() => setShowAddItemModal(false)} 
    />
  </>
  )
}

    