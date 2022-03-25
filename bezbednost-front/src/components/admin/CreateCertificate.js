import Modal from 'react-modal';

const CreateCertificate = ({modalIsOpen, setModalIsOpen}) => {

    return (
        <div>
            <Modal className="fullscreen" isOpen={modalIsOpen} shouldCloseOnEsc={true} onRequestClose={() => setModalIsOpen(false) } ariaHideApp={false}>
                <div className='card w-50 mb-5' 
                    style={{marginLeft: "25%", maxHeight: "600px", marginTop: "4%", borderColor: "#4a6560"}}>
                    <button className="btn-close ms-auto mt-2 me-2" onClick={() => setModalIsOpen(false)}/>
                    <div className='card-body' style={{overflowY: "scroll"}}>
                        <h4 className='card-title' style={{marginTop: "-20px"}}>Create certificate</h4>
                        <div className='title-underline'/>
                        <form className='mt-4'>
                        <label className='form-label'>Certificate type</label>
                        <select className='form-select' required>
                            <option>root</option>
                            <option>intermediate</option>
                            <option>end-entity</option>
                        </select>
                        <label className='form-label mt-3'>Extension</label>
                        <input className='form-control' type="text" required/>
                        <label className='form-label mt-3'>Subject's public key</label>
                        <input className='form-control' type="text" required/>
                        <label className='form-label mt-3'>Valid from</label>
                        <input className='form-control' type="date" required/>
                        <label className='form-label mt-3'>Valid to</label>
                        <input className='form-control' type="date" required/>
                        <label className='form-label mt-3'>Certificate's purpose</label>
                        <textarea className='form-control' type="text" required/>
                        <button type='submit' className='btn mt-4 w-25' onClick={() => setModalIsOpen(false)}
                            style={{marginLeft: "35%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                            Submit
                        </button>
                        </form>
                    </div>
                </div>
            </Modal>
        </div>
    )

}

export default CreateCertificate;