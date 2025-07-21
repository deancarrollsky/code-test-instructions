import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [fullUrl, setFullUrl] = useState('');
  const [customAlias, setCustomAlias] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [urls, setUrls] = useState([]);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/shorten', { fullUrl, customAlias });
      setShortUrl(response.data.shortUrl);
      setError('');
      setCustomAlias('')
      setFullUrl('')
      await fetchUrls();
    } catch (err) {
      setError(err.response?.data?.message || 'Error shortening URL');
    }
  };

  const handleDelete = async (e, url) => {
    e.preventDefault();
    try {
      const response = await axios.delete('http://localhost:8080/' + url.alias, { fullUrl, customAlias });
      setError('');
      await fetchUrls();
    } catch (err) {
      setError(err.response?.data?.message || 'Error shortening URL');
    }
  }
  const fetchUrls = async () => {
    const response = await axios.get('http://localhost:8080/urls');
    setUrls(response.data);
  };

  useEffect(() => {
    fetchUrls();
  }, []);

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">URL Shortener</h1>
      <div className="mb-4">
        <input
          type="text"
          value={fullUrl}
          onChange={(e) => setFullUrl(e.target.value)}
          placeholder="Enter full URL"
          className="border p-2 mr-2"
        />
        <input
          type="text"
          value={customAlias}
          onChange={(e) => setCustomAlias(e.target.value)}
          placeholder="Custom alias (optional)"
          className="border p-2 mr-2"
        />
        <button onClick={handleSubmit} className="bg-blue-500 text-white p-2 rounded">
          Shorten
        </button>
      </div>
      {error && <p className="text-red-500">{error}</p>}
      {shortUrl && <p className="text-green-500">Shortened URL: <a href={shortUrl}>{shortUrl}</a></p>}
      <h2 className="text-xl font-semibold mt-4">Shortened URLs</h2>
      <ul>
        {urls.map((url) => (
          <li key={url.alias} className="my-2">
            <a href={url.shortUrl} className="text-blue-500">{url.alias}</a> - {url.fullUrl}
            <button onClick={e => handleDelete(e, url)} className="bg-blue-500 text-white p-2 m-2 rounded">Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
