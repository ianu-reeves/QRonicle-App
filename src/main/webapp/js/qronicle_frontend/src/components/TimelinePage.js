import React, { useEffect, useState } from 'react'
import Header from "./Header"
import {VerticalTimeline, VerticalTimelineElement } from 'react-vertical-timeline-component'
import 'react-vertical-timeline-component/style.min.css';
import { Stack } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { FaSquare } from 'react-icons/fa';
import { HiArchive } from "react-icons/hi";
import jwtDecode from 'jwt-decode';

export default function TimelinePage() {
  const navigate = useNavigate()
  const [items, setItems] = useState([])

    useEffect(() => {
      const token = localStorage.getItem('token');
        if (!token) {
          navigate('/login')
        }
        const decodedToken = jwtDecode(token);
        //fetch all items for user
        const url = `http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/items/user/${decodedToken.sub}`
        console.log(url)
        const fetchData = async () => {
          try {
            const response = await fetch(`http://18.217.149.55:8080/QRonicle-1.3-DEPLOY/items/user/${decodedToken.sub}`, {
              headers: {
                Authorization: `Bearer ${token}`,
                "Access-Control-Allow-Origin": "*",
              },
            });
            if (!response.ok) {
              throw new Error("Network response was not ok");
            }
            const allItems = await response.json();
            setItems(allItems);
          } catch (error) {
            console.error(error);
          }
        };

        
        fetchData();
      }, []);

      
  return (
    <>
    <Header/>
    <div className='timeline'>
      <VerticalTimeline lineColor='#242424'>
        { items && items
          .sort( (a,b) => a.date > b.date ? 1: -1 )
          .map((item) => (
          <Stack direction='vertical' className='tl-stack'>
            <VerticalTimelineElement className='vert-timeline-element mb-3'
              iconStyle={{background: 'rgb(87, 173, 231)', color: '#fff', boxShadow:'none'}}
              icon={<HiArchive color='white'/>}
              contentStyle={{ background: 'rgb(104, 102, 102)', color: '#fff' }}
              date={item.date.toString().replaceAll(',','-')}>
              <h3>{item.name}</h3>
              <p>{item.description}</p>
              <div className="view-container-tl mt-3">
              <Link to={`/items/${item.id}`} key={item.id} 
                className="view-link-tl">View Item</Link>
            </div>
            </VerticalTimelineElement> 
          </Stack>
          ))
        }
      </VerticalTimeline>
    </div>
    </>
  )
}