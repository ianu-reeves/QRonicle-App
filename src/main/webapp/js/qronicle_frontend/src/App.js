import './App.css';
import {BrowserRouter as Router, Routes, Route } from "react-router-dom"
import Signup from './components/Signup';
import Dashboard from "./components/Dashboard"
import Login from './components/Login';
import Categories from './components/Categories';
import TimelinePage from './components/TimelinePage';
import ForgotPassword from './components/ForgotPassword'
import CategoryPage from './components/CategoryPage';
import ProfileSettings from './components/ProfileSettings';
import ItemPage from './components/ItemPage';
import ProfilePage from './components/ProfilePage';
import MapPage from './components/MapPage';

function App() {  
  return (
    <>
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
    </>
   

  )
}

export default App;

// className="d-flex align-items-center justify-content-center" 
//style={{ minHeight: "100vh" }}