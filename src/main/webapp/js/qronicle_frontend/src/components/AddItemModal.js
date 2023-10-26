import React, { useState } from 'react'
import { Form, Modal, Button, ModalBody, ModalHeader, Alert, Stack } from 'react-bootstrap'

const MAX_COUNT = 10;

export default function AddItemModal({ show, handleClose }) {
    const [error, setError] = useState('')
    const [name, setName] = useState('')
    const [description, setDescription] = useState('')
    const [stringDate, setStringDate] = useState('')
    const [tags, setTags] = useState([])
    const [newTag, setNewTag] = useState('')
    //images
    const [imgFiles, setImgFiles] = useState([])
    const [imgLimit, setImgLimit] = useState(false)

    function handleSubmit(e) {
        e.preventDefault()
        const date = stringDate.split("-").map(Number)
        const itemForm={name, description, tags, date}
        console.log(itemForm)
        const token = localStorage.getItem("token")

        let data = new FormData();
        //let files = document.querySelector('input[type="file"]');
        data.append("itemForm", new Blob([JSON.stringify(itemForm)], {type: "application/json"}));

        imgFiles.forEach(img => {
        data.append("files", img);
        console.log(img);
        });

        fetch("http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/items", {
            method:"POST",
            body: data,
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
        .then(() => {
            console.log("Item has been added.");
        })
        .catch(error => {
            console.error(error);
        });

        handleClose()
    }
 
    const handleUploadFiles = files => {
        const uploaded = [...imgFiles]
        let limitExceeded = false;
        files.some((file) => {
            uploaded.push(file)
            if (uploaded.length === MAX_COUNT) setImgLimit(true)
            if (uploaded.length > MAX_COUNT) {
                alert(`You can only upload a maximum of ${MAX_COUNT} images`)
                setImgLimit(false)
                limitExceeded = true
                return true
            }
        })
        if (!limitExceeded) setImgFiles(uploaded)
    }

    function handleFileChange(event) {
        const selectedImgs = Array.prototype.slice.call(event.target.files)
        handleUploadFiles(selectedImgs)
        //setImgFiles(imgFiles.concat(event.target.files))
    }

    function handleChange(event) {
        setNewTag(event.target.value.trim())
    }

    function handleTagAdd() {
        //no duplicate tags
        if (!newTag.length) {
            setError('Cannot add empty tag.')
        } else {
            if (!tags.includes(newTag)) {
                const newTags = tags.concat( newTag )
            setTags(newTags)
            setNewTag('')
            console.log(tags)
            } else {
                setError('Tag is already added.')
                window.scrollTo(0,0)
            }
        }
        
    }

    return (
    <Modal show={show} onHide={handleClose}>
        <Form onSubmit={handleSubmit}>
            <ModalHeader closeButton>
                <Stack direction='vertical'>
                    <Modal.Title>New Item</Modal.Title>
                    { error && <Alert variant="danger" >{error}</Alert>}
                </Stack>
            </ModalHeader>
            <ModalBody>
                <Form.Group className="mb-3" controlId="name" >
                    <Form.Label className='form-label'>Name</Form.Label>
                    <Form.Control type="text" value={name} onChange={(e)=>setName(e.target.value)} required />
                </Form.Group>
                <Form.Group className="mb-3" controlId="description">
                    <Form.Label>Description</Form.Label>
                    <Form.Control as="textarea"
                                  value={description} onChange={(e)=>setDescription(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3" controlId="stringDate">
                    <Form.Label className='form-label me-3'>Date</Form.Label>
                    <input type="date"
                                  value={stringDate} onChange={(e)=>setStringDate(e.target.value)} />
                </Form.Group>
                {/* <Form.Group className="mb-3" controlId="location">
                    <Form.Label className="form-label">Location</Form.Label>
                    <Form.Control type="text"
                                  value={location} onChange={(e)=>setLocation(e.target.value)} />
                </Form.Group> */}
                <Form.Group>
                    <Form.Label className='form-label me-3'>Add Images:  </Form.Label>
                    <input className='mt-2' id='fileupload' 
                        type="file" multiple
                        onChange={handleFileChange}
                        disabled={imgLimit}/>
                </Form.Group>
                <div className='img-list my-1'>
                    {imgFiles.map(file => (
                        <div>{file.name}</div>
                    ))}
                </div>
                <Form.Group className="mb-3" controlId="tags">
                    <Form.Label className="form-label">Tags</Form.Label>
                    <Form.Control type="text"
                                  value={newTag} onChange={handleChange} />
                    <Button className=" add-btn mt-2" variant="primary" onClick={handleTagAdd}>Add tag</Button>
                </Form.Group>
                <div >
                <div className="d-flex justify-content-end">
                    <Button className="add-btn" variant="primary" type="submit">Add Item</Button>
                </div>
                <ul className='tag-list'>
                    {
                    tags.map((tag) => (
                        <div className='tag-div py-1 px-2 mx-auto'>
                        <li key={tag} className='tags px-2'>{tag}</li>
                        </div>
                    ))
                    }
                </ul>
                </div>
            </ModalBody>
        </Form>
    </Modal>
  )
}