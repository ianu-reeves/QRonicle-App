import React, { Component } from 'react'
import { Route, Navigate } from 'react-router-dom'
//import { RouteContext } from 'react-router/dist/lib/context';


const RouteGuard = ({ component: Component, ...rest }) => {

    function hasJWT() {
        let flag = false;
        localStorage.getItem("token") ? flag=true : flag=false

        return flag
    }

    return (
        <Route {...rest}
            component={hasJWT() ? <Component {...rest} /> : <Navigate to="/login" />}
    />
    )
}

export default RouteGuard;
