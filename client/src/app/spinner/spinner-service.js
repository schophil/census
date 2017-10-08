import Spinner from 'spin.js';

export default function SpinService(el) {

  this.count = 0;
  this.el = el;
  this.spinner = null;

  this.up = function () {
    if (!PRODUCTION) {
      console.log('Spin up', this);
    }
    this.count++;
    if (this.count == 1) {
      var target = document.getElementById(this.el);
      if (this.spinner == null) {
        this.spinner = new Spinner({
          shadow: false,
          length: 10,
          width: 5,
          radius: 15,
          top: '30%'
        }).spin(target);
      } else {
        this.spinner.spin(target);
      }
    }
  };

  this.down = function () {
    if (!PRODUCTION) {
      console.log('Spin down', this);
    }
    this.count--;
    if (this.count <= 0) {
      if (!PRODUCTION) {
        console.log('Stopping spinner...');
      }
      this.spinner.stop();
    }
  };
}
