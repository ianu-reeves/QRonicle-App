import React, {useMemo} from 'react'
import {GoogleMap, Marker} from '@react-google-maps/api'


export default function Map() {

  const itemsData = [
    {
    name: "Item 1",
    id: "1",
    description:"this is item 1",
    date: "01-01-2001",
    location: "Bali",
    value: 1,
    },
    {
    name: "Item 2",
    id: "2",
    description:"this is item 2",
    date: "02-02-2002",
    location: "Phuket",
    value: 2,
    },
    {
        name: "Item 3",
        id: "3",
        description:"this is item 3",
        date: "03-03-2003",
        location: "Koh Samui",
        value: 3,
    },
    {
    name: "I <3 Germany T-shirt",
    id: "4",
    description:"Bought from a giftshop in Germany",
    date: "[-75.3372987731628, 45.383321536272049]",
    location: "Germany",
    value: 12.50,
    },
    {
    name: "Chocolate bar wrapper",
    id: "5",
    description:"from the chocolate factory in Lucerne",
    date: "05-05-2005",
    location: "Switzerland",
    value: 6,
    },
    {
        name: "Eiffel tower statue",
        id: "6",
        description:"Bought at effiel tower gift shop",
        date: "06-06-2006",
        location: "France",
        value: 4,
    }
  ]

  
  const center = useMemo(() => ({lat: 44,lng:  -80}), []);

  return (
    <>
    <GoogleMap 
    zoom={10} 
      center={center}
      mapContainerClassName="map-container"></GoogleMap>
    <Marker position={{lat:44, lng:-80}}/>
    </>
  )
}
