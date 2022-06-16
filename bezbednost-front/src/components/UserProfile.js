import {useEffect, useState} from 'react';
import NavBar from './NavBar';
import axios from 'axios';
import { Link } from 'react-router-dom';

const UserProfile = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const [isUsing2fa, setIsUsing2fa] = useState(false);

    const [word, setWord] = useState("");
    const [size, setSize] = useState(200);
    const [bgColor, setBgColor] = useState("ffffff");
    const [qrCode, setQrCode] = useState("");
    const [displayQrCode, setDisplayQrCode] = useState(false)

    const [user, setUser] = useState(
        {
            username: "sara",
            name: "Sara Poparic",
            email: "sarapoparic@gmail.com",
            country: "Serbia",
            secret: ""
        });

    const twoFactorAuth = {
        isEnabled: isUsing2fa,
        id: user.id
    }

    useEffect(() => {
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
            axios.get(SERVER_URL + "/users", { headers: headers})
            .then(response => {
                var user = response.data;
                setUser(user);
                setIsUsing2fa(response.data.using2FA);
                console.log(isUsing2fa)
            });
        
        setQrCode(`http://api.qrserver.com/v1/create-qr-code/?data=${word}!&size=${size}x${size}&bgcolor=${bgColor}`);

    }, [isUsing2fa, word, size, bgColor])

    const handleClick = (secret) => {
        setWord("otpauth://totp/PKI?secret=" + secret +"&issuer=PKI");
        setDisplayQrCode(true)
      }

    const enableTwoFactorAuth = () => {
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
        setIsUsing2fa(true);
        twoFactorAuth.isEnabled = true;
        axios.put(SERVER_URL + "/users/twoFactor", twoFactorAuth, { headers: headers })
            .then(response => {
                console.log(response);
            })
    }

    const disableTwoFactorAuth = () => {
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
        setIsUsing2fa(false);
        twoFactorAuth.isEnabled = false;
        console.log(twoFactorAuth);
        axios.put(SERVER_URL + "/users/twoFactor", twoFactorAuth, { headers: headers })
            .then(response => {
                console.log(response);
            })
            setDisplayQrCode(false)
    }

    return (
        <div>
            <NavBar/>
            <div className='card' 
                style={{marginLeft: "5%", width: "90%", height: "800px", marginTop: "1%", borderColor: "#4a6560", fontSize: "18px"}}>
                <div className='card-body' style={{overflowY: "scroll"}}>
                    <h4 className='card-title'>Welcome {user.username}</h4>
                    <div className='title-underline'/>
                    <label className='form-label mt-5'><h6>Username: </h6></label>
                    <label className='form-label mt-5 ms-3'>{user.username}</label>
                    <hr className="bg-light border-3 border-top mt-0" />
                    <label className='form-label mt-1'><h6>Name: </h6></label>
                    <label className='form-label mt-1 ms-3'>{user.name}</label>
                    <hr className="bg-light border-3 border-top mt-0" />
                    <label className='form-label mt-1'><h6>Email: </h6></label>
                    <label className='form-label mt-1 ms-3'>{user.email}</label>
                    <hr className="bg-light border-3 border-top mt-0" />
                    <label className='form-label mt-1'><h6>Country: </h6></label>
                    <label className='form-label mt-1 ms-3'>{user.country}</label>
                    <hr className="bg-light border-3 border-top mt-0" />
                    <label className='form-label mt-1'><h6>Two Factor Authentication: </h6></label> 
                    <label className='form-label mt-1 ms-3'> 
                        {!isUsing2fa &&  <div style={{color: "red"}}> disabled </div>}
                        {isUsing2fa && <div style={{color: "green"}}>enabled</div>}
                    </label> <br></br>
                    <label className='form-label mt-1 ms-3'> 
                        {!isUsing2fa && <a style={{ color: "#0a2882", textDecoration: "none", fontWeight: "500" }} onClick={(e) => enableTwoFactorAuth()}>ENABLE</a>}
                        {isUsing2fa && <a style={{ color: "#0a2882", textDecoration: "none", fontWeight: "500" }} onClick={(e) => disableTwoFactorAuth()}>DISABLE</a>}
                    </label> <br></br>
                    <label className='form-label mt-1 ms-3'> 
                        { isUsing2fa && <div>Your code is: {user.secret}</div> }
                    </label> <br></br>
                    <label className='form-label mt-1 ms-3'> 
                        {isUsing2fa && 
                            <div>
                                <a style={{ color: "#0a2882", textDecoration: "none", fontWeight: "500" }} onClick={(e) => handleClick(user.secret)}>Generate QR</a> <br></br>
                            </div>
                        }
                    </label> <br></br>
                    <label className='form-label mt-1 ms-3'> 
                        {displayQrCode && <img src={qrCode} alt="" />}
                    </label>

                    <hr className="bg-light border-3 border-top mt-0" />
                    <Link to="/changePassword" style={{ color: "#4a6560" }}>Change password</Link>
                    <hr className="bg-light border-3 border-top mt-0" />
                </div>
            </div>
        </div>
    )

}

export default UserProfile;