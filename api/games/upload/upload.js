document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('uploadForm');
  const inputFile = document.getElementById('fileInput');
  const blobUrlContainer = document.getElementById('blobUrl');

  form.addEventListener('submit', async (event) => {
    event.preventDefault();

    if (!inputFile.files || !inputFile.files[0]) {
      throw new Error("No file selected");
    }

    const file = inputFile.files[0];

    try {
      const response = await fetch(`/api/avatar/upload?filename=${file.name}`, {
        method: 'POST',
        body: file,
      });

      const newBlob = await response.json();

      if (blobUrlContainer) {
        blobUrlContainer.innerHTML = `Blob url: <a href="${newBlob.url}">${newBlob.url}</a>`;
      }
    } catch (error) {
      console.error(error);
    }
  });
});
