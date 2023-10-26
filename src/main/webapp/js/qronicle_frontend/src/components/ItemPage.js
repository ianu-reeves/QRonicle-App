import React, { useState, useEffect } from 'react'
import Header from './Header'
import { useNavigate, useParams } from 'react-router-dom'
import { Button, Stack, Card, Modal, ModalBody } from 'react-bootstrap'
import QRCode from 'qrcode'
import { saveAs } from 'file-saver'
import EditItemModal from './EditItemModal'

export default function ItemPage() {
    const [showEditItemModal, setShowEditItemModal] = useState(false)
    const [showDeleteItemModal, setShowDeleteItemModal] = useState(false)
    const [error, setError] = useState('')
    const [qrUrl, setQrUrl] = useState('')
    const { id } = useParams()
    const [item, setItem] = useState()
    const [imageArray, setImageArray] = useState([]);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [isLoading, setIsLoading] = useState(true)
    const navigate = useNavigate()
    const backArrow = '<'
    const nextArrow = '>'

    const handleEditClose = () => {
        setShowDeleteItemModal(false)
        navigate(`/items/${id}`)
    }
    const generateQrCode = async () => {
    try {
        const itemUrl = `http://qronicle.frontend.s3-website-us-west-2.amazonaws.com/items/${id}`
        //console.log(itemUrl)
        const response = await QRCode.toDataURL(itemUrl)
        setQrUrl(response)
        //console.log(qrUrl)
    } catch (error) {
        console.log(error)
    }}

    const downloadQr = () => {
        saveAs(qrUrl, `${item.name}qr.png`)
    }

    const nextImage = () => {
        setCurrentImageIndex((currentImageIndex + 1) % imageArray.length) 
    };
  
    const prevImage = () => {
        setCurrentImageIndex((currentImageIndex + imageArray.length - 1) % imageArray.length)
    };

    const handleClickDelete = () => {
        setShowDeleteItemModal(true)
    }

    const handleDeleteItem = () => {
        console.log('delete item now')
        setError("")
        const token = localStorage.getItem('token');
        //add user to database
        try {
            fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/items/${id}`, {
                method:"DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Access-Control-Allow-Origin": "*"
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .catch(error => {
                console.error('There was a problem with the delete operation:', error);
            });
        } catch{
            console.log(error)
        }
        setShowDeleteItemModal(false)
        navigate('/profile')
    }
    
 useEffect( () => {
   
        if (!localStorage.getItem("token")) {
            navigate('/login')
          } 
        generateQrCode()
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
            setItem(data) 
            console.log('item:', data)
            setIsLoading(false)
        })
        .catch(error => {
            console.error(error);
            setIsLoading(false)
            
        });

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
    }, []);
       
    if (isLoading) {
        return (
            <div>Loading...</div>
        )
    }

  return (
    <>
    
    <Header />
    <Card className='mt-2'>
        <Card.Body>
            <Card.Title className="page-title d-flex justify-content-between 
            align-items-baseline fw-normal mb-3">
                <div className="text-end">{item.name}</div>
            </Card.Title>
            <Stack direction='vertical' gap='2'className='mt-4 d-flex' >
                <div className='me-auto'>Item Id: {id}</div>
                <hr className='line-break'/>
                <div className="d-flex align-items-baseline">{item.date.toString().replaceAll(',','-')}</div>
                <hr className='line-break'/>
                <div>{item.description}</div>
                <hr className='line-break'/>
                {item.tags && item.tags.length > 0 && (
                    <ul className='tag-list'>
                        { item.tags.map((tag) => (
                            <div className='tag-div py-1 px-2 mx-auto'>
                            <li key={tag} className='tags px-2'>{tag.description}</li>
                            </div>
                        ))}
                    </ul>
                )}
                {imageArray.length > 0 ? (
                    <Stack direction = 'horizontal' className='me-1'>
                      <button className='img-btn' onClick={prevImage}>{backArrow}</button>
                      <img className='mx-auto' src={`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/files/images/${imageArray[currentImageIndex].fileName}`}  style={{width: "300px", height: "auto"}}/>
                      <button className='img-btn' onClick={nextImage}>{nextArrow}</button>
                    </Stack>
                ) : (
                    <div>No images found.</div>
                )}
                <Stack direction="horizontal" gap="2" className="mt-4">
                  <Button className="ms-auto add-btn mx-1 px-4" variant="outline-primary" 
                    onClick={() => downloadQr()}>Download QR Code</Button>
                    <Button className="ms-auto add-btn" variant="outline-primary" 
                        onClick={() => setShowEditItemModal(true)}>Edit Item</Button>
                </Stack>
                {qrUrl ? (<img src={qrUrl} alt='img' className="mx-auto d-block"/>) : null}   
                <Button className='mx-auto delete-btn' variant='danger' 
                    onClick={handleClickDelete}>Delete Item</Button>
            </Stack>
        </Card.Body>
    </Card>
    {/* Confirm Delete Item Modal */}
    <Modal show={showDeleteItemModal} >
        <Modal.Header>
            <Modal.Title className='page-title'>Delete Item</Modal.Title>
        </Modal.Header>
        <ModalBody>Are you sure you want to delete item #{id}?</ModalBody>
        <Modal.Footer>
            <Button className='btn-secondary' onClick={handleEditClose}>Cancel</Button>
            <Button className='btn-danger' onClick={handleDeleteItem}>Confirm Delete</Button>

        </Modal.Footer>
    </Modal>
    <EditItemModal show={showEditItemModal} 
    handleEditClose={() => setShowEditItemModal(false)} />
    </>
    )
}
