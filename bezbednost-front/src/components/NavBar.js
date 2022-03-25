import { useState } from "react";
import { Link } from "react-router-dom";
import CreateCertificate from "./admin/CreateCertificate";

const NavBar = () => {

    const [createCertifikate, setCreateCertificate] = useState(false);

    return (

        <nav className="navbar navbar-expand-lg navbar-light mt-3 me-5">
            <div className="collapse navbar-collapse">
                <ul className="navbar-nav ms-auto me-5 mb-2 mb-lg-0">
                    <Link to="/admin" className="nav-item" style={{textDecoration:"none", color: "black"}}>HOME</Link>
                    <li role="button" className="nav-item ms-5" onClick={() => setCreateCertificate(true)}>CREATE CERTIFICATE</li>
                    <Link to="/certificates" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>ALL CERTIFICATES</Link>
                    <Link to="/" className="nav-item ms-5" style={{textDecoration:"none", color: "black"}}>LOG OUT</Link>
                </ul>
            </div>

            <CreateCertificate modalIsOpen={createCertifikate} setModalIsOpen={setCreateCertificate} />
        </nav>

    )

}

export default NavBar;