import { useState } from "react";


const TwoFALogin = () => {

    const [code, setCode] = useState("");

    return (
        <div>
        <div className='card ms-auto me-5' style={{width: "40%", "marginTop": "13%", borderColor: "#4a6560"}} >
            <div className='card-body'>
                <h4 className='card-title'>Log in</h4>
                <div className='title-underline'/>
                <form className='mt-4 text-start'>
                    <label className='form-label mt-2'>2FA token</label>
                    <input className='form-control' required type="text" placeholder='Enter code' value={code} onChange={(e) => setCode(e.target.value)} />
                    <button type='submit' className='btn mt-4 w-25' 
                        style={{marginLeft: "38%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                        Log in
                    </button> <br />
                </form>
            </div>
        </div>
    </div>
    )

}

export default TwoFALogin;