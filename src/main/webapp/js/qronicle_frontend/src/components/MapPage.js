import React, {useState, useEffect} from 'react'
import { Container } from 'react-bootstrap';
import Header from "./Header"
import {GoogleMap, useLoadScript, Marker} from '@react-google-maps/api'
import Map from './Map.js'
import { useNavigate } from 'react-router';
//import ReactMapGL, { Marker, Popup } from "react-map-gl"
//import { Marker, Popup } from 'mapbox-gl';


export default function MapPage() {
  const navigate = useNavigate()

    const { isLoaded } = useLoadScript({
        googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    })
  
    if (!isLoaded) return <div>Loading...</div>

  return (
    <>
    <Header/>
    <Map/>
    </>
  )
}
