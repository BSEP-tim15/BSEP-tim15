import axios from "axios";
import { useEffect, useState } from "react";
import { useToasts } from 'react-toast-notifications';

const PasswordlessLogin = () => {
    
    const SERVER_URL = process.env.REACT_APP_API;
    const url = window.location.href;

    const [message, setMessage] = useState("");
    const [isTokenValid, setIsTokenValid] = useState(false);

    useEffect( () => {
        const splitted = url.split("/");
        axios.get(SERVER_URL + "/users/validateToken?" + splitted[splitted.length-1])
        .then(response => {
            if(response.status == 204) {
                setIsTokenValid(false);
                setMessage("Your token has expired, please request new!");
            } else if(response.status == 200) {
                setIsTokenValid(true);
                setMessage("");
            }
        })
    }, [])

    return (
        <div className='card' style={{"width": "55%", "marginTop": "10%", "marginLeft": "25%"}} >
            <h1>{message}</h1>
        </div>
    )
}

export default PasswordlessLogin;