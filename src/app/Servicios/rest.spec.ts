import { TestBed } from '@angular/core/testing';

import { Rest } from './rest';

describe('Rest', () => {
  let service: Rest;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Rest);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
