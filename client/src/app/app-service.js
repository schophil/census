import axios from 'axios';
import census from './census';

export function AppService() {

  this.getApps = function () {
    return axios.get('/rest/subjects');
  };
}

export function MockAppService() {

  this.getApps = function () {
    var data = [
      { id: 'egames', name: 'eGames' },
      { id: 'epis', name: 'EPIS' },
      { id: 'protocol', name: 'ProtocolAPI' }
    ];
    var p = new Promise(
      function (resolve, reject) {
        window.setTimeout(
          function () {
            resolve({ data: data} );
          }, census.mockDelay);
        }
      );
      return p;
    };
  }

export default AppService;
