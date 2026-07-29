import { useState } from "react";

import documentService from "../../services/documentService";

function DocumentModal({
                           document,
                           onClose,
                           onSuccess
                       }) {

    // Form alanlarını tutar.
    const [title, setTitle] = useState(
        document?.title ?? ""
    );

    const [description, setDescription] = useState(
        document?.description ?? ""
    );

    const [file, setFile] = useState(null);

    // Backend hata mesajını tutar.
    const [error, setError] = useState("");

    // Formu gönderir.
    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const formData = new FormData();

            formData.append("title", title);
            formData.append("description", description);

            if (file) {

                formData.append("file", file);

            }

            // Güncelleme işlemi
            if (document) {

                await documentService.updateDocument(
                    document.id,
                    formData
                );

            }
            // Yeni belge oluşturma
            else {

                await documentService.uploadDocument(
                    formData
                );

            }

            await onSuccess();

            onClose();

        } catch (error) {

            console.error(error);

            if (error.response?.data?.message) {

                setError(error.response.data.message);

            } else {

                setError(

                    document
                        ? "Update failed."
                        : "Upload failed."

                );

            }

        }

    };

    return (

        <div className="modal d-block">

            <div className="modal-dialog">

                <div className="modal-content">

                    <div className="modal-header">

                        <h5>

                            {

                                document
                                    ? "Update Document"
                                    : "Upload Document"

                            }

                        </h5>

                    </div>

                    <form onSubmit={handleSubmit}>

                        <div className="modal-body">

                            {

                                error && (

                                    <div className="alert alert-danger">

                                        {error}

                                    </div>

                                )

                            }

                            <input
                                className="form-control mb-3"
                                placeholder="Title"
                                value={title}
                                onChange={(e) =>
                                    setTitle(e.target.value)
                                }
                            />

                            <textarea
                                className="form-control mb-3"
                                placeholder="Description"
                                value={description}
                                onChange={(e) =>
                                    setDescription(e.target.value)
                                }
                            />

                            <input
                                className="form-control"
                                type="file"
                                onChange={(e) =>
                                    setFile(e.target.files[0])
                                }
                            />

                        </div>

                        <div className="modal-footer">

                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={onClose}
                            >

                                Cancel

                            </button>

                            <button
                                type="submit"
                                className="btn btn-success"
                            >

                                {

                                    document
                                        ? "Update"
                                        : "Upload"

                                }

                            </button>

                        </div>

                    </form>

                </div>

            </div>

        </div>

    );

}

export default DocumentModal;