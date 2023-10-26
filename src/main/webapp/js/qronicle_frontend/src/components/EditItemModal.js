import React, { useState, useEffect } from 'react'
import { Form, Modal, Button, ModalBody, ModalHeader } from 'react-bootstrap'
import { useNavigate, useParams } from 'react-router-dom'

export default function EditItemModal({ show, handleEditClose }) {
    const { id } = useParams()
    const [item, setItem] = useState()
    const [isLoading, setIsLoading] = useState(true)
    const navigate = useNavigate()
    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [stringDate, setStringDate] = useState('')
    const [ownerName, setOwnerName] = useState('')

    function handleSubmit(e) {
        e.preventDefault()
        const token = localStorage.getItem("token")
        
        const date = stringDate.split("-").map(Number)
        console.log('date for submit', date)
        const item={id, name, date, description, ownerName}
        console.log('item', item)
        fetch(`http://localhost:8080/items`, {
            method:"PUT",
            body: JSON.stringify(item),
            headers: {"Content-Type":"application/json",
                "Authorization": `Bearer ${token}`
            }
        })
        .then(() => {
            console.log("Item has been edited.");
        })
        .catch(error => {
            console.error(error);
        });
        handleEditClose()
        navigate(`/items/${id}`)
    }
 
   

    useEffect( () => {
        if (!localStorage.getItem("token")) {
            navigate('/login')
          }
        const token = localStorage.getItem("token")
        //get item
        fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/items/${id}`, {
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
            console.log("Response item data:", data);
            setItem(data)
            setName(data.name)
            setDescription(data.description)
            setOwnerName(data.ownerName)
            const paddedDate = data.date.map(num => {
                return num.toString().padStart(2, '0');
              });
            const date = paddedDate.join('-')
            setStringDate(date)
            setIsLoading(false)
        })
        .catch(error => {
            console.error(error);
            setIsLoading(false)
        });
     
    }, []);
       
    if (isLoading) {
        return (
            <div>Loading...</div>
        )
    }

    return (
    <Modal show={show} onHide={handleEditClose}>
        <Form onSubmit={handleSubmit}>
            <ModalHeader closeButton>
                <Modal.Title>Edit Item {id}</Modal.Title>
            </ModalHeader>
            <ModalBody>
                <Form.Group className="mb-3" controlId="name" >
                    <Form.Label className='form-label'>Name</Form.Label>
                    <Form.Control type="text" value={name} placeholder={item.name}onChange={(e)=>setName(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3" controlId="description">
                    <Form.Label>Description</Form.Label>
                    <Form.Control as="textarea"
                                  value={description} placeholder={item.description} onChange={(e)=>setDescription(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3" controlId="stringDate">
                    <Form.Label className='form-label me-3'>Date</Form.Label>
                    <input type="date"
                                  value={stringDate} 
                                  placeholder={item.date.toString().replaceAll(',','-')}
                                  onChange={(e)=>setStringDate(e.target.value)} />
                </Form.Group>
                <div className="d-flex justify-content-end">
                    <Button className="add-btn" variant="primary" type="submit">Submit Edit</Button>
                </div>
            </ModalBody>
        </Form>
    </Modal>
  )
}



