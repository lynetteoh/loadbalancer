import http from 'k6/http';
import { check } from 'k6';


export const options = {
  scenarios: {
    load_test1: {
      executor: 'constant-arrival-rate',
      duration: '60s', // total duration
      preAllocatedVUs: 20 , // to allocate concurrent users 

      rate: 100, // number of constant iterations given `timeUnit`
      timeUnit: '1s',
    },
  },
};

export default function () {
  const payload = JSON.stringify({
    game : 'Mobile Legends',
    gamerID: 'GYUTDTE',
    points: 20, 
    testing: true
  });
  const headers = { 'Content-Type': 'application/json'};
  const res = http.post('http://host.docker.internal:8888/v1/route', payload, {headers});
  console.log(res.status);

  check(res, {
    'Post status is 200': (r) => res.status === 200,
    'Post Content-Type header': (r) => res.headers['Content-Type'] === 'application/json',
  });
}
