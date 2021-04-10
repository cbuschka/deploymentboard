const config = {
  webapiBaseUrl: "/api"
};

class HttpClient {

  doGet(path, options = {
    defaultErrorHandling: true
  }) {
    return fetch(config.webapiBaseUrl + path, {
      method: 'GET',
      credentials: 'include',
      headers: this._getHeaders()
    }).then((response) => {
      if (options.defaultErrorHandling && !response.ok) {
        throw new Error(response.statusText);
      }

      return response;
    });
  }

  doDelete(path, options = {
    defaultErrorHandling: true
  }) {
    return fetch(config.webapiBaseUrl + path, {
      method: 'DELETE',
      credentials: 'include',
      headers: this._getHeaders()
    }).then((response) => {
      if (options.defaultErrorHandling && !response.ok) {
        throw new Error(response.statusText);
      }

      return response;
    });
  }

  doPost(path, body = {}, options = {
    defaultErrorHandling: true
  }) {
    let fetchPromise;
    if (body && body.constructor && body.constructor.name === 'FormData') {
      const headers = this._getHeaders();
      delete headers['Content-Type'];
      fetchPromise = fetch(config.webapiBaseUrl + path, {
        method: 'POST',
        credentials: 'include',
        headers: headers,
        body: body
      });
    } else {
      fetchPromise = fetch(config.webapiBaseUrl + path, {
        method: 'POST',
        credentials: 'include',
        headers: this._getHeaders(),
        body: JSON.stringify(body)
      });
    }

    return fetchPromise.then((response) => {
      if (options.defaultErrorHandling && !response.ok) {
        throw new Error(response.statusText);
      }

      return response;
    });
  }

  doPut(path, body = {}, options = {
    defaultErrorHandling: true
  }) {
    return fetch(config.webapiBaseUrl + path, {
      method: 'PUT',
      credentials: 'include',
      headers: this._getHeaders(),
      body: JSON.stringify(body)
    }).then((response) => {
      if (options.defaultErrorHandling && !response.ok) {
        throw new Error(response.statusText);
      }

      return response;
    });
  }

  _getHeaders() {
    const headers = {
      'Content-Type': 'application/json'
    };

    return headers;
  }
}

export const httpClient = new HttpClient();
