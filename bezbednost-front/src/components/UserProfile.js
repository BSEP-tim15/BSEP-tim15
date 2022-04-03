import {useState} from 'react';
import NavBar from './NavBar';

const UserProfile = () => {

    const [user, setUser] = useState(
        {
            username: "sara",
            name: "Sara Poparic",
            email: "sarapoparic@gmail.com",
            country: "Serbia",
            role: "admin"
        });

    return (
        <div>
            <NavBar/>
            <div className='card' 
                style={{marginLeft: "5%", width: "90%", height: "500px", marginTop: "1%", borderColor: "#4a6560", fontSize: "18px"}}>
                <div className='card-body' style={{overflowY: "scroll"}}>
                    <h4 className='card-title'>Welcome user</h4>
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
                    <label className='form-label mt-1'><h6>Coutry: </h6></label>
                    <label className='form-label mt-1 ms-3'>{user.country}</label>
                    <hr className="bg-light border-3 border-top mt-0" />
                    <label className='form-label mt-1'><h6>Role: </h6></label>
                    <label className='form-label mt-1 ms-3'>{user.role}</label>
                    <hr className="bg-light border-3 border-top mt-0" />
                </div>
            </div>
        </div>
    )

}

export default UserProfile;