import axios from "axios";
import { useState } from "react";
import { useToasts } from 'react-toast-notifications';

const ForgotPassword = () => {

    const SERVER_URL = process.env.REACT_APP_API;

    const { addToast } = useToasts();
    const [email, setEmail] = useState("");

    const sendCode = (e) => {
        var emailDto = {
            email: email
        }
        e.preventDefault();
        console.log(SERVER_URL + "/users/resetPassword")
        console.log(emailDto)
        axios.post(SERVER_URL + "/users/resetPassword", {email: email})
            .then(response => {
                addToast("Code is sent to your email!", { appearance: "success" });
             })
    }

    return (
        <div className='card' style={{"width": "50%", "marginTop": "10%", "marginLeft": "25%"}} >
            <div className='card-body'>
            <h4 className='card-title'>Forgot password</h4>
                <div className='title-underline'/>
                <form className='mt-4 text-start' onSubmit={(e) => sendCode(e)}>
                    <label className='form-label'>Email</label>
                    <input className='form-control' type="text" required placeholder='Enter email' value={email} onChange={(e) => setEmail(e.target.value)} />
                    <button type='submit' className='btn mt-4 w-25' 
                        style={{marginLeft: "38%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                        Send code
                    </button>
                </form>
            </div>
        </div>
    )

}

export default ForgotPassword;