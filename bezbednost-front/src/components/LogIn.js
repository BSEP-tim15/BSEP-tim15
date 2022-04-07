import { Link } from 'react-router-dom';
import '../App.css'
import { useNavigate } from "react-router-dom";
import { useState } from 'react';
import axios from 'axios';
import { useToasts } from 'react-toast-notifications';

const LogIn = () => {

    const SERVER_URL = process.env.REACT_APP_API; 
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const credentials = {
        username,
        password
    }
    const { addToast } = useToasts();
    const navigate = useNavigate();

    const logIn = () => {
        axios.post(SERVER_URL + "/auth/login", credentials)
            .then(response => {
                let token = response.data.accessToken;
                console.log(token);
                localStorage.setItem('jwtToken', token);
                navigate("/certificates");
            })
            .catch(error => {
                addToast("Wrong username or password. Please try again.", { appearance: "error" });
                setUsername("");
                setPassword("");
            });

        navigate("/certificates");
    }

    return (
        <div className='card ms-auto me-5' style={{width: "40%", "marginTop": "13%", borderColor: "#4a6560"}} >
            <div className='card-body'>
                <h4 className='card-title'>Log in</h4>
                <div className='title-underline'/>
                <form className='mt-4 text-start' onSubmit={() => logIn()}>
                    <label className='form-label'>Username</label>
                    <input className='form-control' type="text" placeholder='Enter username' value={username} onChange={(e) => setUsername(e.target.value)} />
                    <label className='form-label mt-2'>Password</label>
                    <input className='form-control' type="password" placeholder='Enter password' value={password} onChange={(e) => setPassword(e.target.value)} />
                    <button type='submit' className='btn mt-4 w-25' 
                        style={{marginLeft: "38%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                        Log in
                    </button>
                </form>
            </div>
        </div>
    )

}

export default LogIn;