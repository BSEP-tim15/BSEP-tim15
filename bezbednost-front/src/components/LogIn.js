import { Link } from 'react-router-dom';
import '../App.css'
import { useNavigate } from "react-router-dom";

const LogIn = () => {

    const navigate = useNavigate();

    const logIn = () => {
        navigate("/admin");
    }

    return (
        <div className='card ms-auto me-5' style={{width: "40%", "marginTop": "13%", borderColor: "#4a6560"}} >
            <div className='card-body'>
                <h4 className='card-title'>Log in</h4>
                <div className='title-underline'/>
                <form className='mt-4 text-start' onSubmit={() => logIn()}>
                    <label className='form-label'>Username</label>
                    <input className='form-control' type="text" />
                    <label className='form-label mt-2'>Password</label>
                    <input className='form-control' type="password" />
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