import axios from "axios";
import { useEffect, useState } from "react";

const Verification = () => {
    const SERVER_URL = process.env.REACT_APP_API;
    const url = window.location.href;
    const [message, setMessage] = useState("");
    const [code, setCode] = useState("");

    useEffect( () => {
        const splitted = url.split("/");
        setCode(splitted[splitted.length-1]);
        console.log(code)
        axios.get(SERVER_URL + "/users/verify?" + code)
        .then(response => {
            setMessage(response.data);
            console.log(response.data)
        })
    }

    )
    return (
        <div className='card' style={{"width": "50%", "marginTop": "10%", "marginLeft": "25%"}} >
            <h1>{message}</h1>
        </div>
    )
}
export default Verification;