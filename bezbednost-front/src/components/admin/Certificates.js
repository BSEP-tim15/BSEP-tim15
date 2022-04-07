import { useEffect, useState } from "react";
import axios from "axios";
import { format } from 'date-fns';
import NavBar from "../NavBar";
import validImg from "../../images/valid.png";
import invalidImg from "../../images/invalid.png";
import SingleCertificate from "./SingleCertificate";
import { useToasts } from "react-toast-notifications";
import Passwords from "../modals/Passwords";

const Certificates = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const {addToast} = useToasts();

    const [passwordsModal, setPasswordsModal] = useState(false);
    const [singleCertificate, setSingleCertificate] = useState(false);
    const [serialNumber, setSerialNumber] = useState(0);

    const [certificateType, setCertificateType] = useState("");
    const [certificates, setCertificates] = useState([]);

    const [revoked, setRevoked] = useState(false);

    useEffect(() => {

        setCertificates([]);
        setPasswordsModal(true);

    }, [])

    useEffect(() => {

        var certificate = {
            certificateType: certificateType, 
            rootPassword: localStorage.rootPassword, 
            intermediatePassword: localStorage.intermediatePassword, 
            endEntityPassword: localStorage.endEntityPassword
        }

        axios.post(SERVER_URL + "/certificates/certificates", certificate)
            .then(response => {
                setCertificates(response.data);
            })

    }, [certificateType, revoked])

    const showSingleCertificate = (serialNumber) => {
        setSerialNumber(serialNumber);
        setSingleCertificate(true);
    }

    const revokeCertificate = (serialNumber) => {
        var serialNumberDto = {
            serialNumber: serialNumber
        }

        axios.post(SERVER_URL + "/certificates/revoke", serialNumberDto)
            .then(response => {
                console.log(response.data);
                setRevoked(!revoked);
            });
    }

    const allCertificates = (
        certificates.map(certificate => (
                <tr className="tableRow" key={certificate.serialNumber} >
                    <td onClick={() => showSingleCertificate(certificate.serialNumber)}>{certificate.serialNumber}</td>
                    <td onClick={() => showSingleCertificate(certificate.serialNumber)}>{certificate.subject.substring(3)}</td>
                    <td onClick={() => showSingleCertificate(certificate.serialNumber)}>{certificate.issuer.substring(3)}</td>
                    <td onClick={() => showSingleCertificate(certificate.serialNumber)}>{format(certificate.validFrom, 'dd.MM.yyyy.')}</td>
                    <td onClick={() => showSingleCertificate(certificate.serialNumber)}>{format(certificate.validTo, 'dd.MM.yyyy.')}</td>
                    <td onClick={() => showSingleCertificate(certificate.serialNumber)}><img className="icon ms-2" src={`${certificate.valid ? validImg : invalidImg}`}/></td>
                    <td> { certificate.valid && <button className='btn' style={{height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "15px"}}
                            onClick={() => revokeCertificate(certificate.serialNumber)}> Revoke </button> } </td>
                </tr>
        ))
    )

    return (
        <div>
            <NavBar/>
            <div className='card' style={{marginLeft: "5%", width: "90%", maxHeight: "550px", marginTop: "1%", borderColor: "#4a6560"}}>
                <div className='card-body' style={{overflowY: "scroll"}}>
                    <h4 className='card-title'>Certificates</h4>
                    <div className='title-underline'/>
                    <div style={{display: "flex"}}>
                        <span className="mt-4">Filter by type:</span>
                        <button className='btn mt-3 ms-3' onClick={() => setCertificateType("root")}
                            style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            Root
                        </button>
                        <button className='btn mt-3 ms-3' onClick={() => setCertificateType("intermediate")}
                            style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            Intermediate
                        </button>
                        <button className='btn mt-3 ms-3' onClick={() => setCertificateType("end-entity")}
                            style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            End-entity
                        </button>
                    </div>
                    <table className="table mt-4">
                        <thead>
                            <tr>
                                <th style={{width: "13%"}}>Certificate ID</th>
                                <th style={{width: "13%"}}>Subject</th>
                                <th style={{width: "13%"}}>Issuer</th>
                                <th style={{width: "13%"}}>Valid from</th>
                                <th style={{width: "13%"}}>Valid to</th>
                                <th style={{width: "13%"}}>Status</th>
                                <th style={{width: "13%"}}>Revocation</th>
                            </tr>
                        </thead>
                        <tbody>
                            {allCertificates}
                        </tbody>
                    </table>
                </div>
            </div>

            <Passwords modalIsOpen={passwordsModal} setModalIsOpen={setPasswordsModal} />
            <SingleCertificate modalIsOpen={singleCertificate} setModalIsOpen={setSingleCertificate} serialNumber={serialNumber} setSerialNumber={setSerialNumber} />
        </div>
    )

}

export default Certificates;