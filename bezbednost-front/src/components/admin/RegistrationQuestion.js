import { useState } from 'react';
import Modal from 'react-modal';
import SubjectRegistration from './SubjectRegistration';

const RegistrationQuestion = ({modalIsOpen, setModalIsOpen, username, role}) => {

    const [registration, setRegistration] = useState(false);

    const closeModal = () => {
        setModalIsOpen(false);
        window.location.reload();
    }

    const registerSubject = () => {
        setRegistration(true);
        setModalIsOpen(false);
    }


    return (
        <div>
            <Modal className="fullscreen" isOpen={modalIsOpen} shouldCloseOnEsc={true} onRequestClose={() => setModalIsOpen(false) } ariaHideApp={false}>
                <div className='card text-center' 
                    style={{marginLeft: "32%", width: "35%", marginTop: "15%", borderColor: "#4a6560"}}>
                    <button className="btn-close ms-auto mt-2 me-2" onClick={() => setModalIsOpen(false)}/>
                    <div className='card-body' style={{overflowY: "scroll"}}>
                        Do you want to register new subject?
                        <div style={{display: "flex"}}>
                            <button  className='btn mt-4 w-25' onClick={() => closeModal()}
                                style={{marginLeft: "23%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                                No
                            </button>
                            <button  className='btn mt-4 w-25' onClick={() => registerSubject()}
                                style={{marginLeft: "5%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                                Yes
                            </button>
                        </div>
                    </div>
                </div>
            </Modal>

            <SubjectRegistration modalIsOpen={registration} setModalIsOpen={setRegistration} username={username} role={role} />
        </div>
    )

}

export default RegistrationQuestion;