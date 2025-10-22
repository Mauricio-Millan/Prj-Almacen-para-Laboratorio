import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaletaColores } from './paleta-colores';

describe('PaletaColores', () => {
  let component: PaletaColores;
  let fixture: ComponentFixture<PaletaColores>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaletaColores]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaletaColores);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
