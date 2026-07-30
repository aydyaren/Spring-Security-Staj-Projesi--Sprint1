import { useEffect, useState } from "react";

import {
    FilePlus,
    FileText,
    Type,
    AlignLeft,
    Upload
} from "lucide-react";

import documentService from "../../services/documentService";

import "./DocumentModal.css";

function DocumentModal({

                           // Güncellenecek belge
                           document: selectedDocument,

                           onClose,

                           onSuccess

                       }) {

    // Başlık bilgisi
    const [title, setTitle] = useState(

        selectedDocument?.title ?? ""

    );

    // Açıklama bilgisi
    const [description, setDescription] = useState(

        selectedDocument?.description ?? ""

    );

    /** @type {[File | null, Function]} */
    const [file, setFile] = useState(null);

    // Backend hata mesajı
    const [error, setError] = useState("");

    // Buton loading durumu
    const [loading, setLoading] = useState(false);

    // ESC ile modalı kapatır.
    useEffect(() => {

        const handleKeyDown = (event) => {

            if (event.key === "Escape") {

                onClose();

            }

        };

        // Browser document nesnesi
        window.document.addEventListener(

            "keydown",

            handleKeyDown

        );

        return () => {

            window.document.removeEventListener(

                "keydown",

                handleKeyDown

            );

        };

    }, [onClose]);

    // Form gönderilir.
    const handleSubmit = async (e) => {

        e.preventDefault();

        setLoading(true);

        setError("");

        if (!selectedDocument && !file) {

            setError("Please select a document.");

            setLoading(false);

            return;

        }

        try {

            const formData = new FormData();

            formData.append(

                "title",

                title

            );

            formData.append(

                "description",

                description

            );

            if (file instanceof File) {

                formData.append(
                    "file",
                    file
                );

            }

            // Güncelleme işlemi
            if (selectedDocument) {

                await documentService.updateDocument(

                    selectedDocument.id,

                    formData

                );

            }

            // Yeni yükleme işlemi
            else {

                await documentService.uploadDocument(

                    formData

                );

            }

            await onSuccess();

            onClose();
        } catch (error) {

            console.error(error);

            // Backend hata mesajı varsa gösterilir.
            if (error.response?.data?.message) {

                setError(

                    error.response.data.message

                );

            }

            // Genel hata mesajı
            else {

                setError(

                    selectedDocument
                        ? "Update failed."
                        : "Upload failed."

                );

            }

        }

        finally {

            setLoading(false);

        }

    };
    useEffect(() => {

        document.body.style.overflow = "hidden";

        return () => {

            document.body.style.overflow = "";

        };

    }, []);

    return (

        // Arka plan
        <div

            className="document-modal-overlay"

            onClick={onClose}

        >

            {/* Modal */}

            <div

                className="document-modal"

                onClick={(e) => e.stopPropagation()}

            >

                <div className="document-modal-content">

                    {/* Header */}

                    <div className="document-modal-header">

                        <div className="modal-title">

                            <FilePlus size={28} />

                            <div>

                                <h5>

                                    {

                                        selectedDocument

                                            ? "Update Document"

                                            : "Upload Document"

                                    }

                                </h5>

                                <p>

                                    {

                                        selectedDocument

                                            ? "Update your document information."

                                            : "Upload a new document."

                                    }

                                </p>

                            </div>

                        </div>

                    </div>

                    {/* Form */}

                    <form onSubmit={handleSubmit}>

                        <div className="document-modal-body">

                            {

                                error && (

                                    <div className="alert alert-danger">

                                        {error}

                                    </div>

                                )

                            }

                            {/* Title */}

                            <label className="form-label">

                                <Type size={16} />

                                Title

                            </label>

                            <input

                                className="document-input"

                                placeholder="Enter title"

                                value={title}

                                onChange={(e) =>

                                    setTitle(

                                        e.target.value

                                    )

                                }

                            />

                            {/* Description */}

                            <label className="form-label">

                                <AlignLeft size={16} />

                                Description

                            </label>

                            <textarea

                                className="document-input"

                                rows="4"

                                placeholder="Enter description"

                                value={description}

                                onChange={(e) =>

                                    setDescription(

                                        e.target.value

                                    )

                                }

                            />
                            {/* Dosya */}

                            <label className="form-label">

                                <FileText size={16} />

                                Document

                            </label>

                            <div className="file-upload-box">

                                <label
                                    htmlFor="documentFile"
                                    className="file-upload-label"
                                >

                                    <Upload size={20} />

                                    <span>

                                        {

                                            file

                                                ? file.name

                                                : (

                                                    selectedDocument

                                                        ? "Choose a new file (optional)"

                                                        : "Choose a file"

                                                )

                                        }

                                    </span>

                                </label>

                                <input

                                    id="documentFile"

                                    type="file"

                                    accept=".pdf,.doc,.docx,.jpeg,.jpg,.png"

                                    onChange={(e) => {

                                        const selectedFile = e.target.files?.[0];

                                        if (!selectedFile) {

                                            return;

                                        }

                                        // İzin verilen uzantılar
                                        const allowedExtensions = [

                                            "pdf",

                                            "doc",

                                            "docx",

                                            "jpg",

                                            "jpeg",

                                            "png"

                                        ];

                                        const extension = selectedFile.name
                                            .split(".")
                                            .pop()
                                            ?.toLowerCase();

                                        if (!allowedExtensions.includes(extension)) {

                                            setError(

                                                "Invalid file type. Allowed: PDF, DOC, DOCX, JPG, JPEG and PNG."

                                            );

                                            setFile(null);

                                            e.target.value = "";

                                            return;

                                        }

                                        // Maksimum 10 MB
                                        const maxSize = 10 * 1024 * 1024;

                                        if (selectedFile.size > maxSize) {

                                            setError("File size cannot exceed 10 MB.");

                                            setFile(null);

                                            e.target.value = "";

                                            return;

                                        }

                                        setError("");

                                        setFile(selectedFile);

                                    }}

                                />

                            </div>

                        </div>

                        {/* Footer */}

                        <div className="document-modal-footer">

                            <button

                                type="button"

                                className="document-button-secondary"

                                onClick={onClose}

                                disabled={loading}

                            >

                                Cancel

                            </button>

                            <button

                                type="submit"

                                className="document-button-primary"

                                disabled={loading}

                            >

                                {

                                    loading

                                        ? (

                                            selectedDocument

                                                ? "Updating..."

                                                : "Uploading..."

                                        )

                                        : (

                                            selectedDocument

                                                ? "Update Document"

                                                : "Upload Document"

                                        )

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