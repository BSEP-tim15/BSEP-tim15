import { useEffect, useState } from "react";
import axios from "axios";
import { format } from 'date-fns';
import NavBar from "../NavBar";
import validImg from "../../images/valid.png";
import invalidImg from "../../images/invalid.png";
import SingleCertificate from "../admin/SingleCertificate";
import { useToasts } from "react-toast-notifications";
import Passwords from "../modals/Passwords";

const EndEntityCertificates = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const {addToast} = useToasts();

    const [singleCertificate, setSingleCertificate] = useState(false);
    const [serialNumber, setSerialNumber] = useState(0);

    const [certificates, setCertificates] = useState([]);

    useEffect(() => {

        var certificate = {
            certificateType: "end-entity", 
            rootPassword: localStorage.rootPassword, 
            intermediatePassword: localStorage.intermediatePassword, 
            endEntityPassword: localStorage.endEntityPassword
        }

        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
            axios.get(SERVER_URL + "/users", { headers: headers})
            .then(response => {
                var user = response.data;

                /*axios.post(SERVER_URL + "/certificates/endEntityCertificates/" + user.id, certificate)
                    .then(response => {
                        setCertificates(response.data);
                    })*/

                axios.post(SERVER_URL + "/certificates/certificates", certificate)
                    .then(response => {
                        setCertificates(response.data);
                    })
            });

    }, [])

    const showSingleCertificate = (serialNumber) => {
        setSerialNumber(serialNumber);
        setSingleCertificate(true);
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
                    <table className="table mt-4">
                        <thead>
                            <tr>
                                <th style={{width: "13%"}}>Certificate ID</th>
                                <th style={{width: "13%"}}>Subject</th>
                                <th style={{width: "13%"}}>Issuer</th>
                                <th style={{width: "13%"}}>Valid from</th>
                                <th style={{width: "13%"}}>Valid to</th>
                                <th style={{width: "13%"}}>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {allCertificates}
                        </tbody>
                    </table>
                </div>
            </div>

            <SingleCertificate modalIsOpen={singleCertificate} setModalIsOpen={setSingleCertificate} serialNumber={serialNumber} setSerialNumber={setSerialNumber} />
        </div>
    )

}

export default EndEntityCertificates;