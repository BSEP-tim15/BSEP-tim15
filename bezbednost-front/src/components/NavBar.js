import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import CreateCertificate from "./admin/CreateCertificate";
import axios from "axios";
import CreateCertificateByIntermediate from "./intermediate/CreateCertificateByIntermediate";
import { useToasts } from "react-toast-notifications";

const NavBar = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const {addToast} = useToasts();

    const [role, setRole] = useState("");
    const [createCertifikate, setCreateCertificate] = useState(false);
    const [createIntermediateCertifikate, setCreateIntermediateCertificate] = useState(false);

    const [user, setUser] = useState({});

    useEffect(() => {

        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
            axios.get(SERVER_URL + "/users", { headers: headers})
            .then(response => {
                var user = response.data;
                setUser(user);
                axios.get(SERVER_URL + `/users/getRole/${user.id}`, {headers:headers})
                    .then(response => {
                        setRole(response.data);
                    });
            });

    }, [])

    const createCertificate = () => {
        var certificate = {
            certificateType: "", 
            rootPassword: localStorage.rootPassword, 
            intermediatePassword: localStorage.intermediatePassword, 
            endEntityPassword: localStorage.endEntityPassword
        }

        if(role !== "ROLE_ADMIN"){
            const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
            axios.post(SERVER_URL + `/certificates/canUserCreateCertificate/${user.username}`, certificate, {headers:headers})
                .then(response => {
                        var valid = response.data;
                        if(valid === true){
                            if(role === "service" || role === "organization"){
                                setCreateIntermediateCertificate(true);
                            } else {
                                setCreateCertificate(true)
                            }
                        } else {
                            addToast("Your certificate is invalid (expired or revoked), so you don't have right to create certificates!", {appearance : "error"});
                        }

                });
        } else {
            setCreateCertificate(true)
        }
    }

    const logout = () => {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('intermediatePassword');
        localStorage.removeItem('rootPassword');
        localStorage.removeItem('endEntityPassword');
    }

    const adminNavBar = (
        <ul className="navbar-nav ms-auto me-5 mb-2 mb-lg-0">
            <Link to="/profile" className="nav-item" style={{textDecoration:"none", color: "black"}}>PROFILE</Link>
            <li role="button" className="nav-item ms-5" onClick={() => createCertificate()}>CREATE CERTIFICATE</li>
            <Link to="/certificates" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>ALL CERTIFICATES</Link>
            <Link to="/" className="nav-item ms-5" onClick={logout} style={{textDecoration:"none", color: "black"}}>LOG OUT</Link>
        </ul>
    )

    const intermediateNavBar = (
        <ul className="navbar-nav ms-auto me-5 mb-2 mb-lg-0">
            <Link to="/profile" className="nav-item" style={{textDecoration:"none", color: "black"}}>PROFILE</Link>
            <li role="button" className="nav-item ms-5" onClick={() => createCertificate()}>CREATE CERTIFICATE</li>
            <Link to="/intermediateCertificates" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>ALL CERTIFICATES</Link>
            <Link to="/" className="nav-item ms-5" onClick={logout} style={{textDecoration:"none", color: "black"}}>LOG OUT</Link>
        </ul>
    )

    const endEntityNavBar = (
        <ul className="navbar-nav ms-auto me-5 mb-2 mb-lg-0">
            <Link to="/profile" className="nav-item" style={{textDecoration:"none", color: "black"}}>PROFILE</Link>
            <Link to="/endEntityCertificates" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>ALL CERTIFICATES</Link>
            <Link to="/" className="nav-item ms-5" onClick={logout} style={{textDecoration:"none", color: "black"}}>LOG OUT</Link>
        </ul>
    )

    return (

        <nav className="navbar navbar-expand-lg navbar-light mt-3 me-5">
            <div className="collapse navbar-collapse">
                {role === "ROLE_ADMIN" && adminNavBar}
                {(role === "ROLE_SERVICE" || role === "ROLE_ORGANIZATION") && intermediateNavBar}
                {role === "ROLE_USER" && endEntityNavBar}
            </div>

            <CreateCertificate modalIsOpen={createCertifikate} setModalIsOpen={setCreateCertificate} />
            <CreateCertificateByIntermediate modalIsOpen={createIntermediateCertifikate} setModalIsOpen={setCreateIntermediateCertificate} />
        </nav>

    )

}

export default NavBar;