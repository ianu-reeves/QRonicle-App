import '../App.css';
import Container from "react-bootstrap/Container"
import {BrowserRouter as Router, Routes, Route, useNavigate } from "react-router-dom"
import Signup from './Signup';
import Dashboard from "./Dashboard"
import Login from './Login';
import Categories from './Categories';
import TimelinePage from './TimelinePage';
import AllItemsPage from './AllItemsPage';
import ForgotPassword from './ForgotPassword'
import CategoryPage from './CategoryPage';
import ProfileSettings from './ProfileSettings';
import ItemPage from './ItemPage';
import ProfilePage from './ProfilePage';
import MapPage from './MapPage';
import { useState } from 'react';

function Routing() {

  //<AUTHPROVIDER> in <Router>
  //Replace Dashboard and updateProfile route with Private Route once currentUser works
  return (
    <Container>
        <Router>
          <Routes>
            <Route path="/signup" element={<Signup />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<Dashboard />} />
            {/* <Route exact path="/" element={<RouteGuard component={<Dashboard/>} />} /> */}
            <Route exact path="/profile-settings" element={<ProfileSettings />} />
            <Route path="/categories" element={<Categories />} />
            <Route path="/timeline" element={<TimelinePage />} />
            <Route path="/map" element={<MapPage />} />
            <Route path="/category/:categoryName" element={<CategoryPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/items/:id" element={<ItemPage />} />
            <Route path="/items/:qrScanResult.text" element={<ItemPage />} />
          </Routes>
        </Router>
    </Container>

  )
}

export default Routing;
