import React from 'react'
import '../App.css'
import { Container, Nav, Navbar } from 'react-bootstrap'
import { Link } from 'react-router-dom'

export default function header() {
  const headerData = [
    { 
    path: '/',
    name: "Home " },
    {
    path: '/categories',
    name: "Categories "},
    {
    path: '/timeline',
    name: "Timeline "},
    {
    path: '/profile',
    name: "Profile "},
  ]

  const handleLogout = (event) => {
    localStorage.removeItem('token');
  }

  return (
    <Navbar collapseOnSelect className='navbar' expand="lg"  variant='dark'>
      <Container>
        <Navbar.Brand href="/" className='brand'>QRonicle</Navbar.Brand>
        <Navbar.Toggle aria-controls='responsive-navbar-nav'/>
        <Navbar.Collapse id='responsive-navbar-nav'>
          <Nav className='ms-auto'>
            {
              headerData.map((item)=>(
                <Link to={item.path} key={item.name} className="header-item">
                  <div>{item.name}  </div>
                </Link>
              ))
            }
          </Nav>
          <Nav className='ms-auto'>
            <Link to="/login" className="logout_btn" onClick={handleLogout}>Log Out</Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}