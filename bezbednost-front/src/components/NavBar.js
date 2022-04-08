import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import CreateCertificate from "./admin/CreateCertificate";
import axios from "axios";
import CreateCertificateByIntermediate from "./intermediate/CreateCertificateByIntermediate";

const NavBar = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const [role, setRole] = useState("");
    const [createCertifikate, setCreateCertificate] = useState(false);
    const [createIntermediateCertifikate, setCreateIntermediateCertificate] = useState(false);

    useEffect(() => {

        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
            axios.get(SERVER_URL + "/users", { headers: headers})
            .then(response => {
                var user = response.data;

                axios.get(SERVER_URL + `/users/getRole/${user.id}`, {headers:headers})
                .then(response => {
                    setRole(response.data);
                });


            });

    }, [])

    const adminNavBar = (
        <ul className="navbar-nav ms-auto me-5 mb-2 mb-lg-0">
            <Link to="/profile" className="nav-item" style={{textDecoration:"none", color: "black"}}>PROFILE</Link>
            <li role="button" className="nav-item ms-5" onClick={() => setCreateCertificate(true)}>CREATE CERTIFICATE</li>
            <Link to="/certificates" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>ALL CERTIFICATES</Link>
            <Link to="/" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>LOG OUT</Link>
        </ul>
    )

    const intermediateNavBar = (
        <ul className="navbar-nav ms-auto me-5 mb-2 mb-lg-0">
            <Link to="/profile" className="nav-item" style={{textDecoration:"none", color: "black"}}>PROFILE</Link>
            <li role="button" className="nav-item ms-5" onClick={() => setCreateIntermediateCertificate(true)}>CREATE CERTIFICATE</li>
            <Link to="/intermediateCertificates" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>ALL CERTIFICATES</Link>
            <Link to="/" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>LOG OUT</Link>
        </ul>
    )

    const endEntityNavBar = (
        <ul className="navbar-nav ms-auto me-5 mb-2 mb-lg-0">
            <Link to="/profile" className="nav-item" style={{textDecoration:"none", color: "black"}}>PROFILE</Link>
            <Link to="/endEntityCertificates" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>ALL CERTIFICATES</Link>
            <Link to="/" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>LOG OUT</Link>
        </ul>
    )

    return (

        <nav className="navbar navbar-expand-lg navbar-light mt-3 me-5">
            <div className="collapse navbar-collapse">
                {role === "admin" && adminNavBar}
                {role === "service" && intermediateNavBar}
                {role === "user" && endEntityNavBar}
            </div>

            <CreateCertificate modalIsOpen={createCertifikate} setModalIsOpen={setCreateCertificate} />
            <CreateCertificateByIntermediate modalIsOpen={createIntermediateCertifikate} setModalIsOpen={setCreateIntermediateCertificate} />
        </nav>

    )

}

export default NavBar;