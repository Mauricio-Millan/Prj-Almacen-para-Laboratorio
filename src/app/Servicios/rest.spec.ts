import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { Rest } from './rest';

describe('Rest', () => {
  let service: Rest;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(Rest);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
