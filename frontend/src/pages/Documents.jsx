import { useEffect, useState } from "react";

import documentService from "../services/documentService";
import DocumentTable from "../components/documents/DocumentTable";
import DocumentModal from "../components/documents/DocumentModal";
import { getCurrentUser } from "../services/authService";
function Documents() {

    // Belge listesini tutar.
    const [documents, setDocuments] = useState([]);

    // Sayfanın yüklenme durumunu tutar.
    const [loading, setLoading] = useState(true);

    // Oluşan hata mesajını tutar.
    const [error, setError] = useState("");

    // Modalın açık olup olmadığını tutar.
    const [showModal, setShowModal] = useState(false);

    // Giriş yapan kullanıcıyı tutar.
    const [currentUser, setCurrentUser] = useState(null);

    // Güncellenecek belgeyi tutar.
    const [selectedDocument, setSelectedDocument] = useState(null);

    // Backend'den bütün belgeleri getirir.
    const loadDocuments = async () => {

        try {

            const response = await documentService.getAllDocuments();

            setDocuments(response);

        } catch (error) {

            console.error(error);

            if (error.response?.data?.message) {

                setError(error.response.data.message);

            } else {

                setError("Failed to load documents.");

            }

        } finally {

            setLoading(false);

        }

    };

    // Belgeyi siler.
    const handleDelete = async (id) => {

        const confirmed = window.confirm(
            "Do you want to delete this document?"
        );

        if (!confirmed) {

            return;

        }

        try {

            await documentService.deleteDocument(id);

            await loadDocuments();

        } catch (error) {

            console.error(error);

            if (error.response?.data?.message) {

                alert(error.response.data.message);

            } else {

                alert("Delete failed.");

            }

        }

    };

    // Giriş yapan kullanıcı bilgilerini getirir.
    const loadCurrentUser = async () => {

        try {

            const response = await getCurrentUser();

            setCurrentUser(response);

        } catch (error) {

            console.error(error);

        }

    };

    // Güncellenecek belgeyi seçer.
    const handleUpdate = (document) => {

        setSelectedDocument(document);

        setShowModal(true);

    };

    useEffect(() => {

        async function loadData() {

            await loadCurrentUser();

            await loadDocuments();

        }

        loadData();

    }, []);

    if (loading) {

        return <h2>Loading...</h2>;

    }

    if (error) {

        return <h2>{error}</h2>;

    }

    return (

        <div className="container mt-5">

            <div className="d-flex justify-content-between align-items-center mb-3">

                <h2>Documents</h2>

                <button
                    className="btn btn-success"
                    onClick={() => {

                        setSelectedDocument(null);

                        setShowModal(true);

                    }}
                >

                    Upload Document

                </button>

            </div>

            <DocumentTable

                documents={documents}

                currentUser={currentUser}

                onDelete={handleDelete}

                onUpdate={handleUpdate}

            />

            {

                showModal && (

                    <DocumentModal

                        document={selectedDocument}

                        onClose={() => {

                            setShowModal(false);

                            setSelectedDocument(null);

                        }}

                        onSuccess={loadDocuments}


                    />

                )

            }

        </div>

    );

}

export default Documents;