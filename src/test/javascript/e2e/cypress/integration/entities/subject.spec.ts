import { login } from '../util/login';

describe('Subject e2e test', () => {
    beforeEach(() => {
        login();
        cy.contains('jhi-home .card-title', 'main').click();
        cy.contains('jhi-projects table tbody td a', 'radar').click();
    });

    it('should load Subjects', () => {
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
    });

    it('should load create Subject dialog', () => {
        cy.get('jhi-subjects button.create-subject').click();
        cy.get('jhi-subject-dialog h4.modal-title').first().should('have.text', 'Create or edit a Subject');
        cy.get('jhi-subject-dialog button.close').click();
    });

    it('should be able to create new subject', () => {
        cy.get('jhi-subjects button.create-subject').click();
        cy.get('jhi-subject-dialog input[name=externalLink]').type('https://radar-base-test.org');
        cy.get('jhi-subject-dialog input[name=externalId]').type('test-subject-1');
        cy.get('jhi-subject-dialog input[name=personName]').type('Test Subject 1');
        cy.get('jhi-subject-dialog input[name=dateOfBirth]').type('1980-01-01');
        cy.get('jhi-subject-dialog select#field_group').select('Test Group B');
        cy.get('jhi-subject-dialog jhi-dictionary-mapper select').first().select('Human-readable-identifier');
        cy.get('jhi-subject-dialog jhi-dictionary-mapper input').first().type('Test Subject 1');
        cy.contains('jhi-subject-dialog jhi-dictionary-mapper button', 'Add').click()
        cy.contains('jhi-subject-dialog button.btn-primary', 'Save').click();
        cy.get('jhi-subjects .subject-row').should('have.length', 21);
    });

    it('should be able to edit a subject', () => {
        cy.get('app-load-more a.load-more-limited').click();
        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().contains('button', 'Edit').click();
        cy.get('jhi-subject-dialog input[name=externalLink]').clear().type('https://radar-base-test-edited.org');
        cy.contains('jhi-subject-dialog button.btn-primary', 'Save').click();
        cy.get('jhi-subjects .subject-row').should('have.length', 29);
    });

    it('should have load subject row with subject-id, external-id, status, project, sources and attributes columns', () => {
        cy.get('app-load-more a.load-more-limited').click();
        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__select-row input')
                .invoke('attr', 'type')
                .should('eq', 'checkbox')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__subject-id .subject-row__field-label')
                .should('have.text', 'Subject Id')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__external-id .subject-row__field-label')
                .should('have.text', 'External Id')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__external-id a')
                .should('have.text',' test-subject-1 ')
                .invoke('attr', 'href')
                .should('eq', 'https://radar-base-test-edited.org')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__status .subject-row__field-label')
                .should('have.text', 'Status')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__status span.badge')
                .should('have.text','ACTIVATED')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__sources .subject-row__field-label')
                .should('have.text', 'Sources')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__attribute-data .subject-row__field-label')
                .should('have.text', 'Attributes')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__attribute-data div span')
                .should('have.text','Human-readable-identifier: Test Subject 1')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__group .subject-row__field-label')
                .should('have.text', 'Group')

        cy.contains('jhi-subjects .subject-row', 'test-subject-1').first().find('.subject-row__content .subject-row__group .subject-row__field-value')
                .should('have.text','Test Group B')
    })

    it('should be able to filter subjects by subject id', () => {
        cy.get('#field-subject-id').type('b-1');
        cy.contains('app-filter-badge', 'Subject Id: b-1').should('exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 11);
        cy.get('#field-subject-id').clear();
        cy.contains('app-filter-badge', 'Subject Id: b-1').should('not.exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
    });

    it('should be able to filter subjects by subject external id', () => {
        cy.get('#field-subject-external-id').type('test-subject-1');
        cy.contains('app-filter-badge', 'External Id: test-subject-1').should('exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 1);
        cy.get('#field-subject-external-id').clear();
        cy.contains('app-filter-badge', 'External Id: test-subject-1').should('not.exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
    });

    it('should be able to filter subjects by human readable id', () => {
        cy.get('#advanced-filter').click();
        cy.get('#field-human-readable-id').type('Test');
        cy.contains('app-filter-badge', 'Human Readable ID: Test').should('exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 1);
        cy.get('#field-human-readable-id').clear();
        cy.contains('app-filter-badge', 'Human Readable ID: Test').should('not.exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
        cy.get('#advanced-filter').click();
    });

    it('should be able to filter subjects by person name', () => {
        cy.get('#advanced-filter').click();
        cy.get('#field-person-name').type('Test');
        cy.contains('app-filter-badge', 'Name: Test').should('exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 1);
        cy.get('#field-person-name').clear();
        cy.contains('app-filter-badge', 'Name: Test').should('not.exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
        cy.get('#advanced-filter').click();
    });

    // TODO Filter by group

    it('should be able to filter subjects by date of birth', () => {
        cy.get('#advanced-filter').click();
        cy.get('#field_date_of_birth').type('1980-01-01');
        cy.contains('app-filter-badge', 'Date of Birth: 1980-01-01').should('exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 1);
        cy.get('#field_date_of_birth').clear();
        cy.contains('app-filter-badge', 'Date of Birth: 1980-01-01').should('not.exist');
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
        cy.get('#advanced-filter').click();
    });

    // TODO Filter by Enrollment Date

    it('should be able to sort subjects by subject id in asc/desc order', () => {
        cy.get('jhi-subjects #field-sort-by').click();
        cy.get('jhi-subjects #sort-by-login').click();
        cy.get('jhi-subjects #field-order-by').click();
        cy.get('jhi-subjects #order-by-desc').click();
        cy.wait(100);
        cy.get('jhi-subjects .subject-row').first().should('contain.text', 'sub-9');
        cy.get('jhi-subjects #field-order-by').click();
        cy.get('jhi-subjects #order-by-asc').click();
        cy.wait(100);
        cy.get('jhi-subjects .subject-row').eq(0).should('contain.text', 'test-subject-1');
    });

    it('should be able to sort subjects by external id in asc/desc order', () => {
        cy.get('jhi-subjects #field-sort-by').click();
        cy.get('jhi-subjects #sort-by-externalId').click();
        cy.wait(100);
        cy.get('jhi-subjects .subject-row').first().should('contain.text', 'sub-1');
        cy.get('jhi-subjects #field-order-by').click();
        cy.get('jhi-subjects #order-by-desc').click();
        cy.wait(100);
        cy.get('jhi-subjects .subject-row').eq(0).should('contain.text', 'test-subject-1');
    });

    it('should be able to delete a subject without source', () => {
        cy.contains('jhi-subjects .subject-row', 'sub-1').find('a').first().click();
        cy.contains('jhi-subject-detail button', 'Delete').click();
        cy.contains('jhi-subject-delete-dialog button', 'Delete').click();
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
    });

    // it('should be able to delete a group', () => {
    //     cy.contains('jhi-project-detail ul.nav-tabs .nav-item', 'Groups').click();
    //     cy.contains('jhi-groups .group-row', 'Test Group C').contains('button', 'Delete').click();
    //     cy.get('jhi-groups .group-row').should('have.length', 2);
    // });

    it('should show number of loaded subjects and total number of subjects', () => {
        cy.contains('jhi-project-detail ul.nav-tabs .nav-item', 'Subjects').click();
        cy.get('app-load-more a.load-more-limited').should('have.text', 'Load more (20/28 shown)');
        cy.get('jhi-subjects .subject-row').should('have.length', 20);
    });

    it('should be able to load subjects on loadMore click', () => {
        cy.get('app-load-more a.load-more-limited').click();
        cy.get('app-load-more span.all-loaded').should('have.text', 'All 28 results loaded');
        cy.get('jhi-subjects .subject-row').should('have.length', 28);
    });
});
